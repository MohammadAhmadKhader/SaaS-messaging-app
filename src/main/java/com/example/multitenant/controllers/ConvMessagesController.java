package com.example.multitenant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.conversationmessages.ConversationMessageUpdateDTO;
import com.example.multitenant.dtos.messages.OrgMessageUpdateDTO;
import com.example.multitenant.models.User;
import com.example.multitenant.services.conversations.ConversationsService;
import com.example.multitenant.services.websocket.WebSocketService;
import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/conversations")
public class ConvMessagesController {
    private final WebSocketService webSocketService;
    private final ConversationsService conversationsService;

    @PutMapping("/{conversationId}/message/{messageId}")
    public ResponseEntity<Object> updateMessageContent(
        @ValidateNumberId @PathVariable Integer messageId,
        @Valid @RequestBody ConversationMessageUpdateDTO dto,
        @ValidateNumberId @PathVariable Integer conversationId
        ) {

        var sender = SecurityUtils.getUserFromAuth();
        var msg = this.conversationsService.updateConvMessageContent(sender, messageId, conversationId, dto.getContent());
        var conv = msg.getConversation();
        var target = AppUtils.getWsTarget(sender, conv);

        this.webSocketService.publishUpdateConvMessage(msg, target);
       
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{conversationId}/message/{messageId}")
    public ResponseEntity<Object> deleteMessage(@ValidateNumberId @PathVariable Integer messageId,
        @ValidateNumberId @PathVariable Integer conversationId) {
        
        var sender = SecurityUtils.getUserFromAuth();
        var conv = this.conversationsService.removeMessageFromConv(messageId, conversationId, sender);
        var target = AppUtils.getWsTarget(sender, conv);

        this.webSocketService.publishDeleteConvMessage(messageId, target);
       
        return ResponseEntity.noContent().build();
    }
}
