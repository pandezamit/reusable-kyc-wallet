package com.lloyds.kyc.repository;

import com.lloyds.kyc.model.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
}
