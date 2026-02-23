#!/bin/bash
# Run the app with Docker (simulates Render environment)
set -e
cd "$(dirname "$0")"
echo "Building Docker image..."
docker build -t home-price-prediction .
echo "Starting container on http://localhost:8080 ..."
docker run --rm -p 8080:8080 -e PORT=8080 home-price-prediction
