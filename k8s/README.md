# Deploying the KYC stack to GKE

Maps your `docker-compose.yml` (canton, daml-init, backend) onto a GKE cluster.
Both services stay **single-replica** because they use in-memory/embedded state
(Canton in-memory storage, backend's H2 DB) — same volatility as your compose
setup, just scaled onto Kubernetes networking. Compose's internal DNS
(service name = hostname) becomes the k8s Service names `canton` and `backend`.

## 0. Prerequisites

```bash
gcloud auth login
gcloud config set project PROJECT_ID
gcloud services enable container.googleapis.com artifactregistry.googleapis.com
```

## 1. Create an Artifact Registry repo and build/push images

```bash
gcloud artifacts repositories create kyc-repo \
  --repository-format=docker \
  --location=REGION

gcloud auth configure-docker REGION-docker.pkg.dev

# from the same folder as your docker-compose.yml
docker build -t REGION-docker.pkg.dev/PROJECT_ID/kyc-repo/canton:latest ./canton
docker push REGION-docker.pkg.dev/PROJECT_ID/kyc-repo/canton:latest

docker build -t REGION-docker.pkg.dev/PROJECT_ID/kyc-repo/daml-init:latest ./daml
docker push REGION-docker.pkg.dev/PROJECT_ID/kyc-repo/daml-init:latest

docker build -t REGION-docker.pkg.dev/PROJECT_ID/kyc-repo/backend:latest ./backend
docker push REGION-docker.pkg.dev/PROJECT_ID/kyc-repo/backend:latest
docker push asia-south1-docker.pkg.dev/ltc-hack2026-team15/kyc-repo/backend:latest
```

Then replace every `REGION-docker.pkg.dev/PROJECT_ID/kyc-repo/...` placeholder
in the three manifest files (`01-canton.yaml`, `02-daml-init-job.yaml`,
`03-backend.yaml`) with your actual image paths.

## 2. Create a GKE cluster

```bash
gcloud container clusters create-auto kyc-cluster \
  --region REGION
# or, for a Standard cluster instead of Autopilot:
# gcloud container clusters create kyc-cluster \
#   --zone REGION-a --num-nodes 2 --machine-type e2-standard-4

gcloud container clusters get-credentials kyc-cluster --region REGION
```

## 3. Deploy

```bash
kubectl apply -k .
```

This creates:
- `kyc-demo` namespace
- `canton` Deployment + ClusterIP Service (ledger/participant/sequencer/mediator ports, internal only)
- `daml-init` Job — waits for `canton:5011` to accept connections, then uploads the DAR and exits (mirrors the commented-out `daml-init` compose service)
- `backend` Deployment + `LoadBalancer` Service exposing port 80 → 8080

## 4. Check status

```bash
kubectl -n kyc-demo get pods,jobs,svc
kubectl -n kyc-demo logs job/daml-init
kubectl -n kyc-demo get svc backend   # EXTERNAL-IP appears once the GCP load balancer is provisioned
```

Once `EXTERNAL-IP` is assigned, the demo UI/API is at `http://EXTERNAL-IP/`.

## Notes / things you'll likely want to adjust

- **Re-running daml-init**: Jobs don't re-run on their own. If you rebuild the
  DAR image, do `kubectl -n kyc-demo delete job daml-init && kubectl apply -f 02-daml-init-job.yaml`.
- **Ports 5011-5020 are internal-only** here (ClusterIP) — matching that only
  `backend` needs to reach Canton. If you need to hit the Ledger API or JSON
  API from outside the cluster (e.g. for local `daml` tooling), either
  `kubectl port-forward svc/canton 5011:5011 -n kyc-demo` or change that
  Service to `LoadBalancer`/add an Ingress.
- **No persistence**: exactly like the compose file, both Canton and the
  backend's H2 DB lose all state on pod restart. If that's not desired long
  term, that's the point where you'd add a Cloud SQL-backed Postgres for
  Canton/participant storage and swap H2 for a managed DB — happy to wire
  that up if you want it.
- **Ingress alternative**: for TLS/a real hostname instead of a bare
  LoadBalancer IP, swap the `backend` Service to `ClusterIP` and add a GCE
  Ingress (`kubernetes.io/ingress.class: gce`) with a `ManagedCertificate`.
