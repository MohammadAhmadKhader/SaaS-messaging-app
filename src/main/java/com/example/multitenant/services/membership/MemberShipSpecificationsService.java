package com.example.multitenant.services.membership;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Membership;
import com.example.multitenant.services.helperservices.ServiceSpecifications;

@Service
public class MemberShipSpecificationsService extends ServiceSpecifications<Membership> {
    public MemberShipSpecificationsService() {
        super(Membership.class);
    }
}