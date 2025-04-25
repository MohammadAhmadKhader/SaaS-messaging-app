package com.example.multitenant.dtos.websocket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketMessage<TPayload> {
    private String event;
    private TPayload payload;
}

