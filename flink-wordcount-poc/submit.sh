#!/usr/bin/env bash
set -e

CLASS="${1:-org.karane.Ex01_BasicWordCount}"
shift 2>/dev/null || true

echo ">>> Submitting $CLASS to Flink cluster..."

docker cp "$(pwd)/app/target/flink-wordcount-1.0.jar" flink-jobmanager-wordcount:/tmp/flink-wordcount.jar
docker cp "$(pwd)/app/data" flink-jobmanager-wordcount:/data

docker exec flink-jobmanager-wordcount \
  flink run \
    --class "$CLASS" \
    /tmp/flink-wordcount.jar "$@"
