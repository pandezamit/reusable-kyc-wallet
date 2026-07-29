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

### Canton file
```bash
https://github.com/digital-asset/canton/releases/download/v3.5.10/canton-open-source-3.5.10.zip
unzip it
cd lib
java -jar canton-open-source-3.5.10.jar --config my-node.conf

health.status
import com.digitalasset.canton.config.RequireTypes.PositiveInt
import com.digitalasset.canton.version.ProtocolVersion
import com.digitalasset.canton.admin.api.client.data.StaticSynchronizerParameters

bootstrap.synchronizer(
   synchronizerName = "my-synchronizer",
   sequencers = sequencers.all,
   mediators = mediators.all,
   synchronizerOwners = sequencers.all,
   synchronizerThreshold = PositiveInt.one,
   staticSynchronizerParameters = StaticSynchronizerParameters.defaultsWithoutKMS(ProtocolVersion.latest),
 )

sequencers.local
mediators.local
participants.local
participants.local.head.synchronizers.connect_local(sequencers.local.head, alias = "my-synchronizer")
```


cd daml
daml build
daml ledger upload-dar --host 34.14.150.251 --port 5011 .\.daml\dist\reusable-kyc-0.0.1.dar
daml start
http://localhost:7575/v2/parties


docker build -t canton-node:3.5.10 .

docker run --rm -it --name canton-node -p 5011:5011 asia-south1-docker.pkg.dev/ltc-hack2026-team15/kyc-repo/canton:latest

java -jar canton-open-source-3.5.10.jar -c my-node.conf --bootstrap bootstrap.canton

gcloud artifacts repositories create kyc-repo   --repository-format=docker   --location=asia-south1

docker build -t asia-south1-docker.pkg.dev/ltc-hack2026-team15/kyc-repo/canton:latest ./canton
