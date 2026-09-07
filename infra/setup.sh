#!/bin/bash
# ==============================================================================
# Cloud Infrastructure Provisioning Script
# Target Platform: Google Cloud Platform (GCP)
# Purpose: Enable ML & Storage APIs, Provision Cloud Storage Bucket
# ==============================================================================

set -e

PROJECT_ID="gen-lang-client-0642163421"
REGION="us-central1"
BUCKET_NAME="enterprise-ai-resume-bucket-0642163421"

echo "Configuring active GCP project to ${PROJECT_ID}..."
gcloud config set project "${PROJECT_ID}"

echo "Enabling necessary Cloud APIs (Storage & Vertex AI)..."
gcloud services enable storage.googleapis.com aiplatform.googleapis.com

echo "Checking if bucket ${BUCKET_NAME} exists..."
if gcloud storage buckets describe "gs://${BUCKET_NAME}" >/dev/null 2>&1; then
    echo "Bucket gs://${BUCKET_NAME} already exists. Skipping creation."
else
    echo "Creating Cloud Storage bucket gs://${BUCKET_NAME} in ${REGION}..."
    gcloud storage buckets create "gs://${BUCKET_NAME}" \
        --location="${REGION}" \
        --uniform-bucket-level-access
    echo "Bucket created successfully."
fi

echo "Infrastructure provisioning complete and ready for application ingestion."