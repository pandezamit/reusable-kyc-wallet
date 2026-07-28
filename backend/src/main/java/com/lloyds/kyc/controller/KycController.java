package com.lloyds.kyc.controller;

import com.lloyds.kyc.model.dto.KycInitiateRequest;
import com.lloyds.kyc.model.entity.KycRecordEntity;
import com.lloyds.kyc.service.KycOrchestrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class KycController {
    private final KycOrchestrationService kycOrchestrationService;

    public KycController(KycOrchestrationService kycOrchestrationService) {
        this.kycOrchestrationService = kycOrchestrationService;
    }

    @PostMapping("/kyc/initiate")
    public ResponseEntity<KycRecordEntity> initiateKyc(
            @RequestPart("request") KycInitiateRequest request,
            @RequestPart("document") MultipartFile document) {
        return ResponseEntity.ok(kycOrchestrationService.initiateKyc(request, document));
    }
}
