package com.example.multitenant.utils;

import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.models.Organization;
import com.example.multitenant.models.User;
import com.example.multitenant.repository.OrganizationsRepository;
import com.example.multitenant.repository.UsersRepository;

public class DataLoader {
    public static Organization loadTestOrganization(Organization org, UsersRepository usersRepository, 
        OrganizationsRepository organizationsRepository) {

        var owner = org.getOwner();
        owner.setPassword("test-password");
        owner.setEmail(owner.getEmail().toLowerCase());
        var savedOwner = usersRepository.saveAndFlush(owner);
        org.setOwner(savedOwner);

        return organizationsRepository.save(org);
    }

    public static List<Organization> loadTestOrganizations(List<Organization> orgs, UsersRepository usersRepository, 
        OrganizationsRepository organizationsRepository) {

        var orgsList = new ArrayList<Organization>();
        for (var organization : orgs) {
            orgsList.add(loadTestOrganization(organization, usersRepository, organizationsRepository));
        }

        return orgsList;
    }

    public static User loadUser(User user, UsersRepository usersRepository) {
        user.setPassword("test-password");
        user.setEmail(user.getEmail().toLowerCase());
        return usersRepository.save(user);
    }

    public static List<User> loadUsers(List<User> users, UsersRepository usersRepository, boolean ignorePassword) {
        for (var u : users) {
            if(ignorePassword) {
                u.setEmail(u.getEmail().toLowerCase());
                u.setPassword("test-password");
            }
        }

        return usersRepository.saveAll(users);
    }

    public static User loadTestUserNoHashedpassword(User user, UsersRepository usersRepository) {
        user.setPassword("test-password");
        return usersRepository.saveAndFlush(user);
    }
}
