package com.example.multitenant.services.membership;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Membership;
import com.example.multitenant.models.binders.MembershipKey;
import com.example.multitenant.repository.MembershipRepository;
import com.example.multitenant.services.helperservices.GenericCrudService;

@Service
public class MemberShipCrudService extends GenericCrudService<Membership, MembershipKey> {
    private MembershipRepository membershipRepository;
    public MemberShipCrudService(MembershipRepository membershipRepository) {
        super(membershipRepository);
        this.membershipRepository = membershipRepository;
    }
}