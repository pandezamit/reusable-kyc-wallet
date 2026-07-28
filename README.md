# Reusable KYC Wallet

## How to Run

### 1. Run Backend (In-Memory DB for demo)
\`\`\`bash
cd backend
mvn spring-boot:run
\`\`\`

### 2. Access APIs
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:kycdb)

### 3. Full Stack with Docker (Postgres, MinIO, Canton)
\`\`\`bash
cd docker
docker-compose up -d
\`\`\`
