package com.lloyds.kyc.repository;

import com.lloyds.kyc.model.entity.KycRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface KycRecordRepository extends JpaRepository<KycRecordEntity, UUID> {
    // MUST be findByCustomerId matching the entity field
    Optional<KycRecordEntity> findByCustomerId(String customerId);
}