package com.example.multitenant.models.logsmodels;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.example.multitenant.models.enums.LogEventType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.*;

@Entity
@Table(name = "events_logs")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "log_type")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private LogEventType eventType;

    @CreationTimestamp
    private Instant timestamp;

    public abstract String getMessage();
}
