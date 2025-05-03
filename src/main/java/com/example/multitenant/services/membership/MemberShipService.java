package com.example.multitenant.services.membership;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.example.multitenant.services.security.*;
import com.example.multitenant.specifications.MembershipSpecifications;
import com.example.multitenant.specificationsbuilders.MembershipSpecificationsBuilder;
import com.example.multitenant.utils.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.dtos.membership.MembershipFilter;
import com.example.multitenant.dtos.organizations.*;
import com.example.multitenant.exceptions.*;
import com.example.multitenant.models.*;
import com.example.multitenant.models.binders.*;
import com.example.multitenant.models.enums.*;
import com.example.multitenant.repository.MembershipRepository;
import com.example.multitenant.services.files.FilesService;
import com.example.multitenant.services.organizations.OrganizationsService;

@RequiredArgsConstructor
@Service
public class MemberShipService {
    private static String defaultSortBy = "joinedAt";
    private static String defaultSortDir = "DESC";

    private final OrganizationRolesService organizationRolesService;
    private final OrganizationPermissionsService organizationsPermissionsService;
    private final MembershipRepository membershipRepository;
    private final OrganizationsService organizationsService;
    private final MemberShipSpecificationsService memberShipSpecificationsService;
    private final MemberShipCrudService memberShipCrudService;
    private final FilesService filesService;

    public Page<Membership> getOrganizaionMemberships(Integer page, Integer size, Integer organizationId) {
        var org = new Organization();
        org.setId(organizationId);
        var pageable = PageRequest.of(page - 1, size, Sort.by("joinedAt","id").descending());
        
        return this.membershipRepository.findByOrganizationAndIsMemberTrue(org, pageable);
    }
    
    public boolean hasUserJoined(Integer orgId, long userId) {
        var membershipKey = new MembershipKey(orgId, userId);
        var memebership = this.memberShipCrudService.findById(membershipKey);
        if(memebership == null) {
            return false;
        }

        return memebership.isMember();
    }

    @Transactional
    public Membership joinOrganization(Integer orgId, long userId) {
        if(orgId == null || orgId<= 0) {
            throw new InvalidOperationException("invalid organization id");
        }

        var oldMembership = this.findOne(orgId, userId);
        if(oldMembership != null && oldMembership.isMember() == true) {
            throw new InvalidOperationException("user is already a member of this organization");
        }

        var membership = oldMembership == null ? new Membership(orgId, userId) : oldMembership;

        var orgUserRole = this.organizationRolesService.
        findByNameAndOrganizationId(DefaultOrganizationRole.ORG_USER.getRoleName(), orgId);
        if(orgUserRole == null) {
            throw new ResourceNotFoundException("organization role");
        }
        
        membership.setOrganizationRoles(List.of(orgUserRole));

        return this.membershipRepository.save(membership);
    }

    @Transactional
    public Membership createOwnerMembership(Organization org, User user) {
        var membership = new Membership(org.getId(), user.getId());
        var createdMembership = this.membershipRepository.save(membership);

        var ownerRole = this.initializeDefaultRolesAndPermissions(org.getId());
        ownerRole.setMemberships(new ArrayList<>());
        ownerRole.getMemberships().add(createdMembership);

        this.organizationRolesService.create(ownerRole);
        
        var orgRoles = new ArrayList<OrganizationRole>();
        orgRoles.add(ownerRole);

        membership.setOrganizationRoles(orgRoles);
        membership.loadDefaults();
        
        return this.membershipRepository.saveAndFlush(membership);
    }

    @Transactional
    public Membership createOrganizationWithOwnerMembership(OrganizationCreateDTO dto, User user) {
        var org = this.organizationsService.create(dto.toModel());
        var membership = new Membership(org.getId(), user.getId());

        var ownerRole = this.initializeDefaultRolesAndPermissions(org.getId());
        var membershipList = new ArrayList<Membership>();
        ownerRole.setMemberships(membershipList);

        this.organizationRolesService.create(ownerRole);
        
        var orgRoles = new ArrayList<OrganizationRole>();
        orgRoles.add(ownerRole);

        membership.setOrganizationRoles(orgRoles);
        
        return this.membershipRepository.saveAndFlush(membership);
    }

    @Transactional
    public List<Membership> swapOwnerShip(Integer orgId, User currOwner, Long newOwnerId) {
        var org = this.organizationsService.findOneWithOwner(orgId);
        if(org == null) {
            throw new ResourceNotFoundException("organization", orgId);
        }

        var newOwnerMembeship = this.findUserMembershipWithRoles(orgId, newOwnerId);
        if(newOwnerMembeship == null || !newOwnerMembeship.isMember()) {
            throw new InvalidOperationException("user must be an organization member");
        }
        
        var owner = org.getOwner();
        if(owner == null || owner.getId() != currOwner.getId()) {
            throw new InvalidOperationException("user must be the owner");
        }

        var currOwnerMembership = this.findUserMembershipWithRoles(orgId, currOwner.getId());
        if(currOwnerMembership == null || !currOwnerMembership.isMember()) {
            throw new UnknownException("error during fetching current owner membership");
        }

        var isRemovedOwnerRoole = currOwnerMembership.getOrganizationRoles().
        removeIf((role) -> role.getName().equals(DefaultOrganizationRole.ORG_OWNER.getRoleName()));

        if(!isRemovedOwnerRoole) {
            throw new UnknownException("failed to remove owner role during transfering ownership");
        }

        var ownerRole = this.organizationRolesService.
        findByNameAndOrganizationId(DefaultOrganizationRole.ORG_OWNER.getRoleName(), orgId);

        var isAddedOwnerRole = newOwnerMembeship.getOrganizationRoles().add(ownerRole);
        if(!isAddedOwnerRole) {
            throw new UnknownException("failed to add owner role durinh transfering ownership");
        }

        this.organizationsService.setOwner(org, newOwnerMembeship.getUser());

        return this.membershipRepository.saveAllAndFlush(List.of(currOwnerMembership, newOwnerMembeship));
    }

    public Membership kickUserFromOrganization(Integer orgId, long userId) {
       try { 
            var membershipKey = new MembershipKey(orgId, userId);

            var orgFuture = CompletableFuture.supplyAsync(() -> this.organizationsService.findById(orgId));
            var membershipFuture = CompletableFuture.supplyAsync(() -> this.memberShipCrudService.findById(membershipKey));

            var membership = membershipFuture.get();
            var organization = orgFuture.get();
            if (membership == null) {
                throw new ResourceNotFoundException("membership");
            }

            if (organization == null) {
                throw new ResourceNotFoundException("organization", orgId);
            }

            membership.setMember(false);
            this.membershipRepository.save(membership);

            return membership; 
        } catch (InterruptedException | ExecutionException e) {
            throw new AsyncOperationException("Error occurred during task execution", e);
        }
    }

    public OrganizationRole assignRole(Integer orgRoleId, Integer orgId, long userId) {
        try {
            var membershipTask = CompletableFuture.supplyAsync(() -> this.findUserMembershipWithRoles(orgId, userId));
            var orgRoleTask = CompletableFuture.supplyAsync(() -> this.organizationRolesService.findOne(orgRoleId));

            var membership = membershipTask.get();
            var orgRole = orgRoleTask.get();

            if(membership == null) {
                throw new ResourceNotFoundException("membership");
            }

            if(orgRole == null || !orgRole.getOrganizationId().equals(orgId)) {
                throw new ResourceNotFoundException("organization role", orgRoleId);
            }

            membership.getOrganizationRoles().forEach((role) -> {
                if(role.getId().equals(orgRoleId)) {
                    throw new InvalidOperationException("user already have the role");
                }
            });

            this.assignRoleToUser(membership, orgRole);
            return orgRole;
        } catch (InterruptedException | ExecutionException ex) {
            throw new AsyncOperationException("Error occurred during task execution", ex);
        }
    }

    public OrganizationRole unAssignRole(Integer orgRoleId, Integer orgId, long userId) {
        try {
            var membershipTask = CompletableFuture.supplyAsync(() -> this.findUserMembershipWithRoles(orgId, userId));
            var orgRoleTask = CompletableFuture.supplyAsync(() -> this.organizationRolesService.findOne(orgRoleId));

            var membership = membershipTask.get();
            var orgRole = orgRoleTask.get();

            if(membership == null) {
                throw new ResourceNotFoundException("membership");
            }

            if(orgRole == null) {
                throw new ResourceNotFoundException("organization role", orgRoleId);
            }

            var hasRole = membership.getOrganizationRoles()
                .stream()
                .anyMatch(role -> role.getId().equals(orgRoleId));

            if (!hasRole) {
                throw new InvalidOperationException("user does not have this role assigned");
            }

            this.unAssignRoleToUser(membership, orgRole);
            return orgRole;
        } catch (InterruptedException | ExecutionException ex) {
            throw new AsyncOperationException("Error occurred during task execution", ex);
        }
    }

    public Membership findOne(Integer orgId, long userId) {
        var membershipKey = new MembershipKey(orgId, userId);
        return this.memberShipCrudService.findById(membershipKey);
    }

    public boolean isMember(Integer orgId, long userId) {
        var membershipKey = new MembershipKey(orgId, userId);
        var probe = new Membership();
        probe.setId(membershipKey);
        probe.setMember(true);

        return this.membershipRepository.exists(Example.of(probe));
    }

    public Membership findUserMembershipWithRolesAndPermissions(Integer orgId, long userId) {
        var organization = new Organization();
        organization.setId(orgId);

        var membership = this.membershipRepository.findUserMembershipWithRolesAndPermissions(organization, userId);
        return membership;
    }

    public List<Long> findUserIdsByOrgIdAndRoleId(Integer orgId, Integer roleId) {
        return this.membershipRepository.findUserIdsByOrgIdAndRoleId(orgId, roleId);
    }

    @Transactional
    public Membership initializeOrganizationWithMembership(Organization org, User owner, MultipartFile image) {
        var createdOrg = this.organizationsService.create(org);
        if(image != null) {
            var fileResponse = this.filesService.uploadFile(image, FilesPath.ORGS_IMAGES);
            createdOrg.setImageUrl(fileResponse.getUrl());
        }
        var membership = this.createOwnerMembership(createdOrg, owner);
        return membership;
    }

    public Membership findUserMembershipWithRoles(Integer orgId, long userId) {
        var membership = this.membershipRepository.findUserMembershipWithRoles(orgId, userId);
        return membership;
    }

    public Membership assignRoleToUser(Membership membership, OrganizationRole orgRole) {
        if(orgRole.getName().equals(DefaultOrganizationRole.ORG_OWNER.getRoleName())) {
            throw new InvalidOperationException("cant assign organization owner to a user");
        }

        membership.getOrganizationRoles().add(orgRole);
        return this.membershipRepository.save(membership);
    }

    public Membership unAssignRoleToUser(Membership membership, OrganizationRole orgRole) {
        if(orgRole.getName().equals(DefaultOrganizationRole.ORG_OWNER.getRoleName())) {
            throw new InvalidOperationException("cant un-assign organization owner from a user");
        }

        if(orgRole.getName().equals(DefaultOrganizationRole.ORG_USER.getRoleName())) {
            throw new InvalidOperationException("cant un-assign organization user from a user");
        }

        var isRemoved = membership.getOrganizationRoles().removeIf((role) -> role.getId().equals(orgRole.getId()));
        if (!isRemoved) {
            throw new UnknownException("an error has occured during attempt to un-assign organization role");
        }

        return this.membershipRepository.save(membership);
    }

    public Page<Membership> findActiveOrgMemberShips(Integer orgId, Integer page, Integer size, String sortBy, String sortDir, MembershipFilter filter) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir, sortBy, sortDir, page, size);
        var spec = MembershipSpecificationsBuilder.build(filter, orgId, true);

        return this.memberShipSpecificationsService.findAllWithSpecifications(pageable, spec,null);
    }

    public long countOrganizationMembers(Integer orgId) {
        return this.membershipRepository.countMembersByOrganizationId(orgId);
    }

    public OrganizationRole initializeDefaultRolesAndPermissions(Integer orgId) {
        var orgOwnerRole = this.createOwnerRole(orgId);
        var orgAdminRole = this.createAdminRole(orgId);
        var orgUserRole = this.createUserRole(orgId);

        this.organizationRolesService.createMany(List.of(orgOwnerRole, orgAdminRole, orgUserRole));

        return orgOwnerRole;
    }

    // * private methods below
    private OrganizationRole createOwnerRole(Integer orgId) {
        var orgOwner = DefaultOrganizationRole.ORG_OWNER;
        var orgOwnerRole = new OrganizationRole(orgOwner.getRoleName());
        orgOwnerRole.setDisplayName("Owner");

        var orgOwnerPerms = this.organizationsPermissionsService.findAllDefaultPermissions(orgOwner);

        orgOwnerRole.setOrganizationPermissions(orgOwnerPerms);
        orgOwnerRole.setOrganizationId(orgId);

        return orgOwnerRole;
    }

    private OrganizationRole createAdminRole(Integer orgId) {
        var orgAdmin = DefaultOrganizationRole.ORG_ADMIN;
        var orgAdminRole = new OrganizationRole(orgAdmin.getRoleName());
        orgAdminRole.setDisplayName("Admin");

        var orgAdminPerms = this.organizationsPermissionsService.findAllDefaultPermissions(orgAdmin);

        orgAdminRole.setOrganizationPermissions(orgAdminPerms);
        orgAdminRole.setOrganizationId(orgId);

        return orgAdminRole;
    }

    private OrganizationRole createUserRole(Integer orgId) {
        var orgUser = DefaultOrganizationRole.ORG_USER;
        var orgUserRole = new OrganizationRole(orgUser.getRoleName());
        orgUserRole.setDisplayName("User");

        var orgUserPerms = this.organizationsPermissionsService.findAllDefaultPermissions(orgUser);

        orgUserRole.setOrganizationPermissions(orgUserPerms);
        orgUserRole.setOrganizationId(orgId);

        return orgUserRole;
    }
}