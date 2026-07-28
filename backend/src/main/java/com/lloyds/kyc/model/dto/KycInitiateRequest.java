package com.lloyds.kyc.model.dto;

import lombok.Data;

@Data
public class KycInitiateRequest {
    private String customerId;
    private String did;
}
