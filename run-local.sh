#!/bin/bash
# Run the app on the port you choose. Usage: ./run-local.sh [port]
# Examples: ./run-local.sh        → http://localhost:8080
#           ./run-local.sh 3000   → http://localhost:3000
#           ./run-local.sh 5000   → http://localhost:5000
cd "$(dirname "$0")"
PORT="${1:-8080}"
echo "Starting on http://localhost:$PORT (Ctrl+C to stop)"
./mvnw spring-boot:run -q -Dspring-boot.run.arguments="--server.port=$PORT"
