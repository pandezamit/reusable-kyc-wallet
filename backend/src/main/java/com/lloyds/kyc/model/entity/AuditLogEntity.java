package com.lloyds.kyc.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLogEntity {
    @Id
    private UUID id;
    private String eventType;
    private String actor;
    private String target;
    private String eventHash;
    private LocalDateTime timestamp;
    private String details;
}
