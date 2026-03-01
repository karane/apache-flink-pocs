# Flink Word Count POC (Java)

Word counting variations with Apache Flink DataStream API: basic counting, file I/O, filtering, transformations, and text analytics.

## Tech Stack

- **Java 11** / **Flink 1.18.1**
- **Maven 3.9** (runs in Docker — no local install needed)
- **Docker & Docker Compose**

## Prerequisites

- Docker & Docker Compose

## Usage

```bash
# 1. Start the Flink cluster
docker compose up -d

# 2. Build the JAR
./build.sh

# 3. Run a specific section
./submit.sh org.karane.Ex01_BasicWordCount

# 4. View Flink UI
# Dashboard: http://localhost:8081

# 5. Tear down
docker compose down
```

## Viewing Output

Job output is written to the TaskManager's stdout log. To view it:

```bash
docker logs flink-taskmanager-wordcount 2>/dev/null | tail -50
```

Or use the **Flink Web UI** at http://localhost:8081:
Task Managers → select the task manager → Stdout tab.

## Sample Data

- `app/data/lorem.txt` — 15 lines about Apache Flink concepts and features
- `app/data/shakespeare.txt` — 15 lines from Hamlet's "To be or not to be" soliloquy
