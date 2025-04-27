package com.example.multitenant.services.friendrequests;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.shared.CursorPage;
import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.exceptions.UnauthorizedUserException;
import com.example.multitenant.models.FriendRequest;
import com.example.multitenant.models.enums.FriendRequestStatus;
import com.example.multitenant.repository.FriendRequestsRepository;
import com.example.multitenant.repository.GenericRepository;
import com.example.multitenant.services.generic.GenericService;
import com.example.multitenant.services.users.UsersService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
public class FriendRequestsService extends GenericService<FriendRequest, Integer> {

    private FriendRequestsRepository friendRequestsRepository;
    private UsersService usersService;

    public FriendRequestsService(FriendRequestsRepository friendRequestsRepository, UsersService usersService) {
        super(friendRequestsRepository);

        this.friendRequestsRepository = friendRequestsRepository;
        this.usersService = usersService;
    }

    public void deleteFriendRequest(Integer requestId, Long currentUserId) {
        var request = this.findById(requestId);
        if (request == null) { 
            throw new ResourceNotFoundException("request", requestId);
        }

        if (!request.getReceiverId().equals(currentUserId) && !request.getSenderId().equals(currentUserId)) {
            throw new AuthorizationDeniedException("you are not allowed to delete this friend request");
        }

        this.friendRequestsRepository.delete(request);
    }

    public CursorPage<FriendRequest, Integer> getUserFriendRequests(Long userId, Integer cursorId, int size) {
        var pageable = PageRequest.of(0, size + 1);

        var requests = this.friendRequestsRepository.findFriendRequestsByReceiver(userId, cursorId, pageable);
        var hasNext = requests.size() > size;
        
        Integer nextCursor;
        if(hasNext) {
            nextCursor = requests.get(size - 1).getId();
            requests = requests.subList(0, size); 
        } else {
            nextCursor = null;
        }

        return CursorPage.of(requests, nextCursor, hasNext);
    }

    public CursorPage<FriendRequest, Integer> getSentFriendRequests(Long userId, Integer cursorId, int size) {
        var pageable = PageRequest.of(0, size + 1);
       
        var requests = this.friendRequestsRepository.findFriendRequestsBySender(userId, cursorId, pageable);
        var hasNext = requests.size() > size;
        
        Integer nextCursor;
        if(hasNext) {
            nextCursor = requests.get(size - 1).getId();
            requests = requests.subList(0, size); 
        } else {
            nextCursor = null;
        }

        return CursorPage.of(requests, nextCursor, hasNext);
    }

    @Transactional
    public void acceptFriendRequest(Integer requestId, Long currentUserId) {
        var request = this.friendRequestsRepository.findOneWithUsers(requestId);
        if (request == null) { 
            throw new ResourceNotFoundException("request", requestId);
        }

        if (!request.getReceiverId().equals(currentUserId)) {
            throw new AuthorizationDeniedException("you can only accept requests sent to you");
        }

        var sender = request.getSender();
        var receiver = request.getReceiver();

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        request.setStatus(FriendRequestStatus.ACCEPTED);

        this.usersService.save(receiver);
        this.usersService.save(sender);
        this.friendRequestsRepository.save(request);
    }

    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        var isAlreadyFriend = this.usersService.isUserFriend(senderId, receiverId);
        if(isAlreadyFriend) {
            throw new InvalidOperationException("users are already friends");
        }

        var existingFriendRequest = this.friendRequestsRepository.existsFriendRequest(senderId, receiverId, FriendRequestStatus.PENDING);
        if(existingFriendRequest) {
            throw new InvalidOperationException("user already has a friend request");
        }

        var sender = this.usersService.findById(senderId);
        if(sender == null) {
            throw new ResourceNotFoundException("user", receiverId);
        }

        var receiver = this.usersService.findById(receiverId);
        if(receiver == null) {
            throw new ResourceNotFoundException("user", senderId);
        }

        if (sender.getId() == receiver.getId()) {
            throw new InvalidOperationException("can not send friend request to yourself");
        }

        var friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequestStatus.PENDING);

        return this.friendRequestsRepository.save(friendRequest);
    }
}
