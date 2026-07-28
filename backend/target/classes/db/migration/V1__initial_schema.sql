CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(255) UNIQUE NOT NULL,
    did VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE kyc_records (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    canton_contract_id VARCHAR(255),
    mitek_dossier_id VARCHAR(255),
    verified_at TIMESTAMP,
    expires_at TIMESTAMP
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    actor VARCHAR(255) NOT NULL,
    target VARCHAR(255),
    event_hash VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    details TEXT
);
