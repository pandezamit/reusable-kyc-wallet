package com.lloyds.kyc.service;

import com.lloyds.kyc.model.entity.AuditLogEntity;
import com.lloyds.kyc.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logEvent(String eventType, String actor, String details) {
        AuditLogEntity log = new AuditLogEntity();
        log.setId(UUID.randomUUID());
        log.setEventType(eventType);
        log.setActor(actor);
        log.setDetails(details);
        log.setEventHash("hash-" + UUID.randomUUID());
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
