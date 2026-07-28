package com.lloyds.kyc.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class MitekIntegrationService {
    public String createDossier(String customerId) {
        return "mitek-dossier-" + UUID.randomUUID();
    }
    public boolean submitDocuments(String dossierId, Object document) {
        return true; // Mock verification success
    }
}
