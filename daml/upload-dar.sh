#!/bin/bash
set -euo pipefail

HOST="${CANTON_HOST:-canton}"
PORT="${CANTON_PORT:-5011}"
MAX_TRIES=40

echo "[daml-init] Waiting for Canton ledger API at ${HOST}:${PORT}..."
tries=0
until nc -z "${HOST}" "${PORT}"; do
  tries=$((tries + 1))
  if [ "${tries}" -ge "${MAX_TRIES}" ]; then
    echo "[daml-init] Gave up waiting for Canton after ${MAX_TRIES} attempts."
    exit 1
  fi
  sleep 3
done

echo "[daml-init] Canton is reachable. Uploading DAR..."
daml ledger upload-dar --host "${HOST}" --port "${PORT}" /daml/reusable-kyc.dar
echo "[daml-init] DAR uploaded successfully. Exiting."
