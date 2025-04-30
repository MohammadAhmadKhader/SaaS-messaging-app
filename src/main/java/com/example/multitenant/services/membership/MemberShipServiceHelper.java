package com.example.multitenant.services.membership;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Membership;
import com.example.multitenant.utils.ServicesHelper;

@Service
public class MemberShipServiceHelper extends ServicesHelper<Membership> {
    public MemberShipServiceHelper() {
        super(Membership.class);
    }
}