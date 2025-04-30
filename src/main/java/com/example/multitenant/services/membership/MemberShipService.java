package com.example.multitenant.services.membership;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.example.multitenant.services.security.*;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.example.multitenant.dtos.organizations.*;
import com.example.multitenant.exceptions.*;
import com.example.multitenant.models.*;
import com.example.multitenant.models.binders.*;
import com.example.multitenant.models.enums.*;
import com.example.multitenant.repository.MembershipRepository;
import com.example.multitenant.services.generic.GenericService;
import com.example.multitenant.services.organizations.OrganizationsService;

@Service
public class MemberShipService extends GenericService<Membership, MembershipKey> {

    private final OrganizationRolesService organizationRolesService;
    private final OrganizationPermissionsService organizationsPermissionsService;
    private final MembershipRepository membershipRepository;
    private final OrganizationsService organizationsService;

    public MemberShipService(MembershipRepository membershipRepository, 
        OrganizationsService organizationsService, 
        OrganizationRolesService organizationRolesService,
        OrganizationPermissionsService organizationsPermissionsService) {

        super(membershipRepository);
        this.membershipRepository = membershipRepository;
        this.organizationsService = organizationsService;
        this.organizationRolesService = organizationRolesService;
        this.organizationsPermissionsService = organizationsPermissionsService;
    }

    public Page<Membership> getOrganizaionMemberships(Integer page, Integer size, Integer organizationId) {
        var org = new Organization();
        org.setId(organizationId);
        var pageable = PageRequest.of(page - 1, size, Sort.by("joinedAt","id").descending());
        
        return this.membershipRepository.findByOrganizationAndIsMemberTrue(org, pageable);
    }
    
    public boolean hasUserJoined(Integer orgId, long userId) {
        var membershipKey = new MembershipKey(orgId, userId);
        var memebership = this.findById(membershipKey);
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
        membership.loadDefaults();

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
        membership.loadDefaults();

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
    public Membership createOrganizationWithOwnerMembership(OrganizationCreateDTO dto, User user) {
        var org = this.organizationsService.create(dto.toModel());
        var membership = new Membership(org.getId(), user.getId());
        membership.loadDefaults();

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
            var membershipFuture = CompletableFuture.supplyAsync(() -> this.findById(membershipKey));

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
        return this.findById(membershipKey);
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

    public Membership findUserMembershipWithRoles(Integer orgId, long userId) {
        var organization = new Organization();
        organization.setId(orgId);
        
        var membership = this.membershipRepository.findUserMembershipWithRoles(organization, userId);
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

    public OrganizationRole initializeDefaultRolesAndPermissions(Integer orgId) {
        var orgOwnerRole = this.createOwnerRole(orgId);
        var orgAdminRole = this.createAdminRole(orgId);
        var orgUserRole = this.createUserRole(orgId);

        this.organizationRolesService.createMany(List.of(orgOwnerRole, orgAdminRole, orgUserRole));

        return orgOwnerRole;
    }

    private OrganizationRole createOwnerRole(Integer orgId) {
        var orgOwner = DefaultOrganizationRole.ORG_OWNER;
        var orgOwnerRole = new OrganizationRole(orgOwner.getRoleName());

        var orgOwnerPerms = this.organizationsPermissionsService.findAllDefaultPermissions(orgOwner);

        orgOwnerRole.setOrganizationPermissions(orgOwnerPerms);
        orgOwnerRole.setOrganizationId(orgId);

        return orgOwnerRole;
    }

    private OrganizationRole createAdminRole(Integer orgId) {
        var orgAdmin = DefaultOrganizationRole.ORG_ADMIN;
        var orgAdminRole = new OrganizationRole(orgAdmin.getRoleName());

        var orgAdminPerms = this.organizationsPermissionsService.findAllDefaultPermissions(orgAdmin);

        orgAdminRole.setOrganizationPermissions(orgAdminPerms);
        orgAdminRole.setOrganizationId(orgId);

        return orgAdminRole;
    }

    private OrganizationRole createUserRole(Integer orgId) {
        var orgUser = DefaultOrganizationRole.ORG_USER;
        var orgUserRole = new OrganizationRole(orgUser.getRoleName());

        var orgUserPerms = this.organizationsPermissionsService.findAllDefaultPermissions(orgUser);

        orgUserRole.setOrganizationPermissions(orgUserPerms);
        orgUserRole.setOrganizationId(orgId);

        return orgUserRole;
    }

    // public void delete(Organization org, User user) {
    //     var membershipKey = new MembershipKey(org.getId(), user.getId());
    //     var membership = this.findById(membershipKey);

    //     membership.getOrganizationRoles().clear();
    //     this.membershipRepository.save(membership);
    //     this.membershipRepository.delete(membership);
    // }
}
