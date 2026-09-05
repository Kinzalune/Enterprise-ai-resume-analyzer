#!/usr/bin/env bash
# ==============================================================================
# Enterprise AI Resume Analyzer - GCP Infrastructure Setup
# Target Services: Google Cloud Storage (GCS), Vertex AI / GenAI APIs
# ==============================================================================

set -euo pipefail

# 1. Variables
PROJECT_ID="gen-lang-client-0642163421"
REGION="us-central1"
BUCKET_NAME="enterprise-ai-resume-bucket-0642163421"

echo "=== Initializing Cloud Infrastructure for [${PROJECT_ID}] ==="

# 2. Set current GCP project context
echo "--> Setting active GCP project..."
gcloud config set project "${PROJECT_ID}"

# 3. Enable Required APIs (Cloud Storage & Vertex AI/Generative Language)
echo "--> Enabling required APIs..."
gcloud services enable storage.googleapis.com \
                       aiplatform.googleapis.com \
                       generativelanguage.googleapis.com

# 4. Create Google Cloud Storage Bucket for Raw Resume Storage
echo "--> Checking if GCS bucket exists..."
if gsutil ls -b "gs://${BUCKET_NAME}" >/dev/null 2>&1; then
    echo "--> Bucket [gs://${BUCKET_NAME}] already exists. Skipping creation."
else
    echo "--> Creating regional GCS bucket [gs://${BUCKET_NAME}] in [${REGION}]..."
    gcloud storage buckets create "gs://${BUCKET_NAME}" \
        --project="${PROJECT_ID}" \
        --location="${REGION}" \
        --uniform-bucket-level-access
    echo "--> Bucket created successfully with Uniform Bucket Level Access."
fi

echo "=== Infrastructure provisioning completed successfully! ==="