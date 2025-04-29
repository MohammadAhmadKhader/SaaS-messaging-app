package com.example.multitenant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.messages.*;
import com.example.multitenant.services.messages.OrgMessagesService;
import com.example.multitenant.services.websocket.WebSocketService;
import com.example.multitenant.utils.AppUtils;
import com.github.javafaker.App;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories/{categoryId}/channels/{channelId}/messages")
public class OrgMessagesController {

    private final OrgMessagesService messagesService;
    private final WebSocketService webSocketService; 
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMessageContent(
        @ValidateNumberId @PathVariable(name = "id") Integer messageId,
        @Valid @RequestBody OrgMessageUpdateDTO dto,
        @ValidateNumberId @PathVariable Integer categotyId
        ) {

        var senderId = AppUtils.getUserIdFromAuth();
        var tenantId = AppUtils.getTenantId();

        var updatedMsg = this.messagesService.updateContent(messageId, dto.getContent(), senderId);
        this.webSocketService.publishUpdatedOrgMessage(updatedMsg, tenantId, categotyId);
       
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMessage(
        @ValidateNumberId @PathVariable(name = "id") Integer messageId,
        @ValidateNumberId @PathVariable Integer categotyId) {
        
        var senderId = AppUtils.getUserIdFromAuth();
        var tenantId = AppUtils.getTenantId();

        this.messagesService.deleteUserMessage(messageId, senderId);
        this.webSocketService.publishDeletedOrgMessage(messageId, tenantId, categotyId);
       
        return ResponseEntity.noContent().build();
    }
}
