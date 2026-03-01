#!/usr/bin/env bash
set -e

echo ">>> Building Word Count POC..."

docker run --rm \
  -v "$(pwd)/app:/app" \
  -v "$HOME/.m2:/root/.m2" \
  -w /app \
  maven:3.9-eclipse-temurin-11 \
  mvn -q clean package -DskipTests

echo ">>> Build complete. JAR located at app/target/"
