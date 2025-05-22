package com.example.multitenant.testsupport.utils;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Membership;
import com.example.multitenant.models.Organization;
import com.example.multitenant.models.User;
import com.example.multitenant.repository.OrgsRepository;
import com.example.multitenant.repository.UsersRepository;
import com.example.multitenant.services.membership.MemberShipService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DataLoaderServce {
    private final UsersRepository usersRepository;
    private final OrgsRepository organizationsRepository;
    private final MemberShipService memberShipService;
    private final TestAuthHelpers testAuthHelpers;
    private String encodedPassword;
    private String testPasswordPlain = "test-password";
    
    @Transactional
    public Organization loadTestOrganization(Organization org, List<Membership> memberships) {
        var owner = org.getOwner();
        owner.setPassword(this.encodePasswordAndCache(this.testPasswordPlain));
        owner.setEmail(owner.getEmail().toLowerCase());
        var savedOwner = this.usersRepository.saveAndFlush(owner);
        
        org.setOwner(savedOwner);

        var savedOrg = this.organizationsRepository.saveAndFlush(org);
        var membership = this.memberShipService.createOwnerMembership(savedOrg, savedOwner);
        memberships.add(membership);

        return savedOrg;
    }

    public static Membership addUserToOrg(Integer orgId, Long userId, MemberShipService memberShipService) {
        return memberShipService.joinOrganization(orgId, userId, false);
    }

    public List<Organization> loadTestOrganizations(List<Organization> orgs, List<Membership> memberships) {
        var orgsList = new ArrayList<Organization>();
        for (var organization : orgs) {
            orgsList.add(this.loadTestOrganization(organization, memberships));
        }

        return orgsList;
    }

    public User loadUser(User user, UsersRepository usersRepository) {
        user.setPassword(this.encodePasswordAndCache(this.testPasswordPlain));
        user.setEmail(user.getEmail().toLowerCase());
        return usersRepository.save(user);
    }

    public List<User> loadUsers(List<User> users) {
        for (var user : users) {
            user.setPassword(this.encodePasswordAndCache(this.testPasswordPlain));
        }
        return this.usersRepository.saveAll(users);
    }

    private String encodePasswordAndCache(String password) {
        if(this.encodedPassword != null) {
            return this.encodedPassword;
        }

        this.encodedPassword = testAuthHelpers.encodePassword(password);
        return this.encodedPassword;
    }
}
