package com.lloyds.kyc.controller;

import com.lloyds.kyc.model.dto.KycInitiateRequest;
import com.lloyds.kyc.model.entity.KycRecordEntity;
import com.lloyds.kyc.repository.KycRecordRepository;
import com.lloyds.kyc.service.KycOrchestrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class KycController {
    private final KycOrchestrationService kycOrchestrationService;
    private KycRecordRepository kycRecordRepository;

    public KycController(KycOrchestrationService kycOrchestrationService, KycRecordRepository kycRecordRepository) {
        this.kycOrchestrationService = kycOrchestrationService;
        this.kycRecordRepository = kycRecordRepository;
    }

    @PostMapping("/kyc/initiate")
    public ResponseEntity<KycRecordEntity> initiateKyc(
            @RequestPart("request") KycInitiateRequest request,
            @RequestPart("document") MultipartFile document) {
        return ResponseEntity.ok(kycOrchestrationService.initiateKyc(request, document));
    }

    // GET endpoint to fetch KYC status for the Digital Wallet
    @GetMapping("/kyc/{clientId}/status")
    public ResponseEntity<?> getKycStatus(@PathVariable String clientId) {
        // MUST call findByCustomerId
        Optional<KycRecordEntity> record = kycRecordRepository.findByCustomerId(clientId);

        if (record.isPresent()) {
            return ResponseEntity.ok(record.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No KYC record found for client ID: " + clientId));
        }
    }
}
