package com.example.multitenant.services.friendrequests;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.FriendRequest;
import com.example.multitenant.repository.FriendRequestsRepository;
import com.example.multitenant.services.helperservices.GenericCrudService;

@Service
public class FriendRequestsCrudService extends GenericCrudService<FriendRequest, Integer> {
    private FriendRequestsRepository friendRequestsRepository;
    public FriendRequestsCrudService( FriendRequestsRepository friendRequestsRepository) {
        super(friendRequestsRepository);
        this.friendRequestsRepository = friendRequestsRepository;
    }
}
