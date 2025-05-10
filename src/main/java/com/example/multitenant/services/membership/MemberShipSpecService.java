package com.example.multitenant.services.membership;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Membership;
import com.example.multitenant.models.binders.MembershipKey;
import com.example.multitenant.services.helperservices.SpecificationsService;

@Service
public class MemberShipSpecService extends SpecificationsService<Membership, MembershipKey> {
    public MemberShipSpecService() {
        super(Membership.class);
    }
}