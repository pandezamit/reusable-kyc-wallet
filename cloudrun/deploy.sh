#!/bin/bash
# Fastest path to a public demo URL: builds backend/Dockerfile with Cloud Build
# and deploys straight to Cloud Run. No Artifact Registry setup needed —
# `--source` handles building and pushing for you.
set -euo pipefail

PROJECT_ID="${PROJECT_ID:?Set PROJECT_ID env var to your GCP project id}"
REGION="${REGION:-asia-south1}"
SERVICE="${SERVICE:-kyc-backend}"

gcloud config set project "${PROJECT_ID}"

gcloud run deploy "${SERVICE}" \
  --source ./backend \
  --region "${REGION}" \
  --platform managed \
  --allow-unauthenticated \
  --min-instances=1 \
  --max-instances=3 \
  --memory=1Gi \
  --port=8080

echo "Deployed. Fetching URL..."
gcloud run services describe "${SERVICE}" --region "${REGION}" --format='value(status.url)'
