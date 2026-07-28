package com.lloyds.kyc.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class DocumentStorageService {
    public String storeDocument(String customerId, Object document) {
        return "s3://kyc-docs/" + customerId + "/" + UUID.randomUUID() + ".pdf";
    }
    public String computeHash(Object document) {
        return "sha256-" + UUID.randomUUID();
    }
}
