# Running & Deploying the KYC Wallet demo

## What was added

- `backend/Dockerfile` — multi-stage Maven build → slim JRE runtime for the Spring Boot API (none existed before).
- `daml/Dockerfile` + `daml/upload-dar.sh` — builds the Daml model into a `.dar` and uploads it into Canton once Canton is reachable.
- `docker-compose.yml` (repo root) — runs all 3 components together.
- `cloudrun/` — Cloud Run deploy configs (`service.yaml`, `cloudbuild.yaml`, `deploy.sh`, plus an optional advanced multi-container variant).

**Important fact about this codebase right now:** `CantonLedgerService`, `DocumentStorageService`, and `MitekIntegrationService` in the backend are fully mocked — they just return random UUID strings and don't actually call Canton, S3/MinIO, or Mitek. The backend also uses an in-memory H2 database, not the Postgres from the old `docker/docker-compose.yml`. That means **the backend already works standalone** — Canton and the Daml model aren't on the request path for the demo UI. Keep this in mind when deciding what to deploy.

---

## 1. Run all 3 locally with Docker Compose

```bash
cd reusable-kyc-wallet-master
docker compose up --build
```

This starts, in order:
1. **canton** — participant + sequencer + mediator (in-memory storage), ledger API on `5011`, admin API on `5012`, JSON API on `5013`.
2. **daml-init** — waits for Canton's ledger API, builds the `.dar` from `daml/`, uploads it, then exits (this is a one-shot job, not a long-running service — seeing it exit with code 0 is expected and correct).
3. **backend** — Spring Boot app on `8080`, depends on Canton being healthy (not because it calls it today, but so the stack comes up in a sane order).

Once it's up:
- Demo UI: http://localhost:8080/
- Swagger: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console

Tear down with `docker compose down`.

If you only want the backend for a quick local check, `docker compose up --build backend` also works on its own since it has no real runtime dependency on Canton.

---

## 2. Deploy to Cloud Run for the hackathon demo

### Recommended: backend only (fastest, most reliable)

Since the backend doesn't actually need Canton at runtime, deploying just the backend gets you a public demo URL with the least moving parts and the smallest chance of breaking right before judging.

**Quickest path — let Cloud Build handle everything:**
```bash
export PROJECT_ID=your-gcp-project-id
export REGION=asia-south1        # or your preferred region
chmod +x cloudrun/deploy.sh
./cloudrun/deploy.sh
```
This runs `gcloud run deploy --source ./backend`, which builds `backend/Dockerfile` with Cloud Build, pushes it, and deploys — no manual Artifact Registry setup required. It prints the public URL at the end.

**Repeatable/CI path — `cloudbuild.yaml`:**
```bash
gcloud artifacts repositories create kyc-repo --repository-format=docker --location=asia-south1  # once
gcloud builds submit --config=cloudrun/cloudbuild.yaml .
```

**Declarative path — `service.yaml`:**
Edit the `image:` placeholder in `cloudrun/service.yaml` to your actual Artifact Registry path, then:
```bash
gcloud run services replace cloudrun/service.yaml --region asia-south1
gcloud run services add-iam-policy-binding kyc-backend --region asia-south1 \
  --member="allUsers" --role="roles/run.invoker"   # if you want it public
```

`min-instances=1` is set in all three so there's no cold-start lag while judges are clicking around — remove it afterward if you want to avoid ongoing cost.

### Optional/advanced: Canton running alongside it

If you specifically want a live Canton node reachable in the same deployment (e.g. to show its admin/health endpoint), see `cloudrun/service-multicontainer.yaml` — it deploys backend + Canton as a multi-container Cloud Run service, with Canton reachable only from the backend over `localhost`. Read the comments at the top of that file first: it needs the DAR baked into the Canton image at build time (Cloud Run won't run the separate one-shot `daml-init` job), and multi-container support needs to be available in your project/region. For a hackathon demo, I'd only reach for this if a judge specifically wants to see Canton itself running live — otherwise it adds risk for no visible payoff right now, given the backend doesn't call it.

---

## 3. If you later wire the backend up to real Canton/MinIO

Right now none of `CANTON_LEDGER_HOST`, `CANTON_LEDGER_PORT` etc. are read by the Java code — they're set in `docker-compose.yml`/Cloud Run configs as placeholders for when you replace the mock service implementations with real gRPC/ledger-api calls. At that point you'd also want to reinstate Postgres (the old `docker/docker-compose.yml` had it) instead of H2, since H2's in-memory data disappears on every container restart.
