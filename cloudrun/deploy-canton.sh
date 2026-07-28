#!/bin/bash
# Deploys Canton (canton/Dockerfile) as its OWN public Cloud Run service, so you
# can build the .dar locally and run `daml ledger upload-dar` against it directly
# from your machine -- no daml-init sidecar/job involved.
#
# Why the extra flags, vs. the plain backend deploy.sh:
#   --use-http2          Ledger API is gRPC; Cloud Run must speak HTTP/2 to the
#                         container for gRPC to work at all.
#   --no-cpu-throttling  Canton's participant/sequencer/mediator run background
#                         work outside of request handling; Cloud Run normally
#                         throttles CPU to ~0 between requests, which would
#                         stall Canton.
#   --min/max-instances=1 storage=memory in my-node.conf means ledger state
#                         lives only in this one running container. Autoscaling
#                         to >1 instance would give you multiple, unsynced
#                         ledgers; scaling to 0 would wipe state entirely.
#   --allow-unauthenticated  So the local `daml` CLI can reach the Ledger API
#                         without setting up IAM-authenticated gRPC. This makes
#                         the raw ledger API public with no auth -- fine for a
#                         short-lived demo, not for anything long-lived.
#
# canton/my-node.conf's ledger-api port now reads `port = ${?PORT}`, so it
# binds to whatever port Cloud Run injects (matches --port below) instead of
# the hardcoded 5011 used by local docker-compose.
set -euo pipefail

PROJECT_ID="${PROJECT_ID:?Set PROJECT_ID env var to your GCP project id}"
REGION="${REGION:-asia-south1}"
SERVICE="${SERVICE:-kyc-canton}"

gcloud config set project "${PROJECT_ID}"

gcloud run deploy "${SERVICE}" \
  --source ./canton \
  --region "${REGION}" \
  --platform managed \
  --allow-unauthenticated \
  --use-http2 \
  --no-cpu-throttling \
  --min-instances=1 \
  --max-instances=1 \
  --concurrency=20 \
  --cpu=2 \
  --memory=2Gi \
  --timeout=300 \
  --port=8080

echo "Deployed. Fetching URL..."
URL=$(gcloud run services describe "${SERVICE}" --region "${REGION}" --format='value(status.url)')
HOST="${URL#https://}"

echo
echo "Canton Ledger API is reachable (gRPC over TLS) at: ${HOST}:443"
echo
echo "From your local machine, build the dar and upload it with:"
echo "  cd daml && daml build -o output.dar"
echo "  daml ledger upload-dar --host ${HOST} --port 443 --tls output.dar"
