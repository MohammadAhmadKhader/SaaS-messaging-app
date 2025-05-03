package com.example.multitenant.controllers;

import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.dtos.friendrequests.FriendRequestSendDTO;
import com.example.multitenant.dtos.friendrequests.FriendRequestViewDTO;
import com.example.multitenant.dtos.users.*;
import com.example.multitenant.models.FriendRequest;
import com.example.multitenant.models.enums.DefaultGlobalRole;
import com.example.multitenant.services.friendrequests.FriendRequestsService;
import com.example.multitenant.services.users.UsersService;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UsersService usersService;
    private final FriendRequestsService friendRequestsService;

    @PutMapping("")
    public ResponseEntity<Object> updateUserProfile(@Valid @RequestBody UserUpdateDTO dto) {
        var principal = SecurityUtils.getPrincipal();
        if(principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        var userId = principal.getUser().getId();;
        var updatedUser = this.usersService.findThenUpdate(userId, dto.toModel());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedUser.toViewDTO());
    }

    @DeleteMapping("/self-delete")
    public ResponseEntity<Object> selfDeleteUser() {
        var principal = SecurityUtils.getPrincipal();
        if(principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        var user = principal.getUser();
        var isSuperAdmin = user.getRoles().stream().anyMatch((role) -> {
            return role.getName().equals(DefaultGlobalRole.SUPERADMIN.getRoleName());
        });

        if(isSuperAdmin) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse(String.format("invalid operation, can't delete super admin")));
        }

        var isDeleted = this.usersService.findThenDeleteById(user.getId());
        if(!isDeleted) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("user", user.getId()));
        }
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/friend-requests/receiver")
    public ResponseEntity<Object> getFriendRequestsByReceiver(@RequestParam(required = false) Integer cursorId, @HandleSize @RequestParam(defaultValue = "20") Integer size) {
        var user = SecurityUtils.getUserFromAuth();
        var cusror = this.friendRequestsService.getUserFriendRequests(user.getId(), cursorId, size);
        var body = cusror.toApiResponse("friendRequests", (frs) -> {
            return frs.stream().map((fr) -> fr.toViewDTO()).toList();
        });

        return ResponseEntity.ok(body);
    }

    @GetMapping("/friend-requests/sender")
    public ResponseEntity<Object> getFriendRequestsBySender(@RequestParam(required = false) Integer cursorId, @HandleSize @RequestParam(defaultValue = "20") Integer size) {
        var user = SecurityUtils.getUserFromAuth();
        var cusror = this.friendRequestsService.getSentFriendRequests(user.getId(), cursorId, size);
        var body = cusror.toApiResponse("friendRequests", (frs) -> {
            return frs.stream().map((fr) -> fr.toViewDTO()).toList();
        });

        return ResponseEntity.ok(body);
    }

    @PostMapping("/friend-requests/send")
    public ResponseEntity<Object> sendFriendRequest(@Valid @RequestBody FriendRequestSendDTO dto) {
        var user = SecurityUtils.getUserFromAuth();
        var friendRequest = this.friendRequestsService.sendFriendRequest(user.getId(), dto.getReceiverId());
        var body = ApiResponses.OneKey("friendRequest", friendRequest.toViewDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PatchMapping("/friend-requests/{friendRequestId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable @ValidateNumberId Integer friendRequestId) {
        var user = SecurityUtils.getUserFromAuth();
        this.friendRequestsService.acceptFriendRequest(friendRequestId, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/friend-requests/{friendRequestId}")
    public ResponseEntity<Void> deleteFriendRequest(@PathVariable @ValidateNumberId Integer friendRequestId) {
        var user = SecurityUtils.getUserFromAuth();
        this.friendRequestsService.deleteFriendRequest(friendRequestId, user.getId());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove-friend/{friendId}")
    public ResponseEntity<Void> removeFriendHandler(@PathVariable @ValidateNumberId Long friendId) {
        var user = SecurityUtils.getUserFromAuth();
        this.usersService.removeFriend(user.getId(), friendId);

        return ResponseEntity.noContent().build();
    }
}