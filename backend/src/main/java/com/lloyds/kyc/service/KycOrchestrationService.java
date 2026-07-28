package com.lloyds.kyc.service;

import com.lloyds.kyc.model.dto.KycInitiateRequest;
import com.lloyds.kyc.model.entity.KycRecordEntity;
import com.lloyds.kyc.model.enums.KycStatus;
import com.lloyds.kyc.repository.KycRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class KycOrchestrationService {
    private final CantonLedgerService cantonLedgerService;
    private final MitekIntegrationService mitekIntegrationService;
    private final DocumentStorageService documentStorageService;
    private final AuditService auditService;
    private final KycRecordRepository kycRecordRepository;

    public KycOrchestrationService(CantonLedgerService cantonLedgerService, MitekIntegrationService mitekIntegrationService,
                                   DocumentStorageService documentStorageService, AuditService auditService,
                                   KycRecordRepository kycRecordRepository) {
        this.cantonLedgerService = cantonLedgerService;
        this.mitekIntegrationService = mitekIntegrationService;
        this.documentStorageService = documentStorageService;
        this.auditService = auditService;
        this.kycRecordRepository = kycRecordRepository;
    }

    @Transactional
    public KycRecordEntity initiateKyc(KycInitiateRequest request, MultipartFile document) {
        String customerId = request.getCustomerId();
        KycRecordEntity existingKyc = kycRecordRepository.findByCustomerId(customerId).orElse(null);

        if (existingKyc != null && existingKyc.getStatus() == KycStatus.ACTIVE) {
            auditService.logEvent("KYC_INITIATED", customerId, "Existing Active KYC found. Sharing status.");
            return existingKyc;
        }

        auditService.logEvent("KYC_INITIATED", customerId, "Initiating new KYC via Mitek.");
        String dossierId = mitekIntegrationService.createDossier(customerId);
        String storageRef = documentStorageService.storeDocument(customerId, document);
        String docHash = documentStorageService.computeHash(document);
        boolean verified = mitekIntegrationService.submitDocuments(dossierId, document);
        
        KycRecordEntity kycRecord = new KycRecordEntity();
        kycRecord.setId(UUID.randomUUID());
        kycRecord.setCustomerId(customerId);
        kycRecord.setMitekDossierId(dossierId);
        kycRecord.setVerifiedAt(LocalDateTime.now());
        kycRecord.setExpiresAt(LocalDateTime.now().plusYears(1));
        
        if (verified) {
            String cantonContractId = cantonLedgerService.createKycRecord(customerId, docHash);
            kycRecord.setCantonContractId(cantonContractId);
            kycRecord.setStatus(KycStatus.ACTIVE);
        } else {
            kycRecord.setStatus(KycStatus.REJECTED);
        }
        
        kycRecordRepository.save(kycRecord);
        auditService.logEvent("KYC_COMPLETED", customerId, "Status: " + kycRecord.getStatus());
        return kycRecord;
    }
}
