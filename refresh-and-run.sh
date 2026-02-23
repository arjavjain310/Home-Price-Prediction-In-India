#!/bin/bash
# Clean build and run - use this to see the latest UI changes (dark theme, house image, all states/UTs)
# Usage: ./refresh-and-run.sh [port]   e.g. ./refresh-and-run.sh 3000
set -e
cd "$(dirname "$0")"
PORT="${1:-8080}"
echo "Cleaning and building..."
./mvnw clean compile -q
echo "Starting app on http://localhost:$PORT ..."
./mvnw spring-boot:run -q -Dspring-boot.run.arguments="--server.port=$PORT"
