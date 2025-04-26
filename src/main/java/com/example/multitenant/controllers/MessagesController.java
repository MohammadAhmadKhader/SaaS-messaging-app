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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories/{categoryId}/channels/{channelId}/messages")
public class MessagesController {

    private final OrgMessagesService messagesService;
    private final WebSocketService webSocketService; 
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMessageContent(
        @ValidateNumberId @PathVariable(name = "id") Integer messageId,
        @Valid @RequestBody OrgMessageUpdateDTO dto) {

        var senderId = AppUtils.getUserIdFromAuth();
        var tenantId = AppUtils.getTenantId();

        var updatedMsg = this.messagesService.updateContent(messageId, dto.getContent(), senderId);
        this.webSocketService.publishUpdatedMessage(updatedMsg, tenantId);
       
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMessage(
        @ValidateNumberId @PathVariable(name = "id") Integer messageId) {
        
        var senderId = AppUtils.getUserIdFromAuth();
        var tenantId = AppUtils.getTenantId();

        this.messagesService.deleteUserMessage(messageId, senderId);
        this.webSocketService.publishDeletedMessage(messageId, tenantId);
       
        return ResponseEntity.noContent().build();
    }
}
