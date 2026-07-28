package com.lloyds.kyc.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class CantonLedgerService {
    public String createKycRecord(String customerId, String docHash) {
        return "canton-contract-" + UUID.randomUUID();
    }
    public String createDigitalIdentity(String customerId) {
        return "did:lloyds:" + UUID.randomUUID();
    }
}
