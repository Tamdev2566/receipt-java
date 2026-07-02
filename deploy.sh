#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Application configurations
APP_NAME="receipt-phase"
PORT="8080"

# Stop and remove any previously running container with the same name
echo "🔄 Stopping and removing the old Docker container..."
docker stop $APP_NAME || true
docker rm $APP_NAME || true

# Build a new Docker image using the Dockerfile in the current directory
echo "📦 Building the new Docker Image..."
docker build -t $APP_NAME .

# Run the new container in detached mode (-d) and map the specified port
echo "🚀 Running the Docker Container on Port: $PORT..."
docker run -d -p $PORT:8080 --name $APP_NAME $APP_NAME

# Print success message and the active backend API endpoint URL
echo "✅ Deployment completed successfully!"
echo "🔗 Dashboard Stats URL: http://localhost:$PORT/api/receipts/dashboard/stats"