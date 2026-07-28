package com.lloyds.kyc.model.entity;

import com.lloyds.kyc.model.enums.KycStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kyc_records")
@Data
public class KycRecordEntity {
    @Id
    private UUID id;
    private String customerId;
    @Enumerated(EnumType.STRING)
    private KycStatus status;
    private String cantonContractId;
    private String mitekDossierId;
    private LocalDateTime verifiedAt;
    private LocalDateTime expiresAt;
}
