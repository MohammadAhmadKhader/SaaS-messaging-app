package com.example.multitenant.dtos.logs;

import java.util.UUID;

import com.example.multitenant.models.enums.LogEventType;
import com.example.multitenant.models.logsmodels.BaseLog;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogsViewDTO {
    private UUID id;
    private LogEventType event;
    private String message;
    
    public static LogsViewDTO of(BaseLog log) {
        var logView = new LogsViewDTO();
        logView.setEvent(log.getEventType());
        logView.setId(log.getId());
        logView.setMessage(log.getMessage());

        return logView;
    }
}
