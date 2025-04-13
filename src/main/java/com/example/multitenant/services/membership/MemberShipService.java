package com.example.multitenant.services.membership;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Membership;
import com.example.multitenant.models.binders.MembershipKey;
import com.example.multitenant.repository.MembershipRepository;
import com.example.multitenant.services.generic.GenericService;

@Service
public class MemberShipService extends GenericService<Membership, MembershipKey> {
    private MembershipRepository MembershipRepository;
    public MemberShipService(MembershipRepository MembershipRepository) {
        super(MembershipRepository);
    }
    
    public boolean hasUserJoined(long userId, Integer orgId) {
        var membershipKey = new MembershipKey(orgId, userId);
        return this.MembershipRepository.existsById(membershipKey);
    }
}
