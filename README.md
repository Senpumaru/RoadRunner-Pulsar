# RoadRunner-Pulsar

This repository contains the Apache Pulsar implementation for the RoadRunner project. It's responsible for handling streaming and response-reply systems.

This project contains various tests and examples for Apache Pulsar, including a producer, stress test, request-reply pattern, and separate requester and responder implementations.


## Prerequisites

- Java 17 or later
- Maven 3.6 or later
- Docker and Docker Compose (for running Pulsar)

## Pulsar Setup

1. Create a `docker-compose.yml` file in your project root with the following content:

```yaml
version: '3'
services:
  pulsar:
    image: apachepulsar/pulsar:3.3.1
    ports:
      - "6650:6650"
      - "8080:8080"
    environment:
      - PULSAR_MEM=" -Xms512m -Xmx512m -XX:MaxDirectMemorySize=1g"
    command: bin/pulsar standalone

networks:
  default:
    name: pulsar-network
```

2. Start Pulsar using Docker Compose:

```bash
docker-compose up -d
```

## Project Configuration

Ensure your `pom.xml` file contains the necessary dependencies and plugin configurations as shown in the provided `pom.xml` file.

## Running the Tests

Before running any tests, make sure to compile the project:

```bash
mvn clean compile
```

### 1. Pulsar Producer

Run the basic Pulsar producer:

```bash
mvn exec:java@producer
```

### 2. Stress Test

Run the Pulsar stress test:

```bash
mvn exec:java@stress-test
```

### 3. Request-Reply Pattern

Run the request-reply example:

```bash
mvn exec:java@request-reply
```

### 4. Improved Requester

Run the improved Pulsar requester:

```bash
mvn exec:java@requester
```

### 5. Improved Responder

Run the improved Pulsar responder:

```bash
mvn exec:java@responder
```

## Notes

- Ensure that the Pulsar service is running and accessible at `pulsar://pulsar:6650` before running the tests.
- The requester and responder should be run simultaneously in separate terminal windows for the request-reply pattern to work correctly.
- Adjust the Pulsar connection URL in the Java files if your Pulsar setup differs from the default (`pulsar://pulsar:6650`).

## Troubleshooting

- If you encounter connection issues, ensure that the Pulsar container is running and that your Java application can reach it through the Docker network.
- Check the logs of the Pulsar container for any startup issues:
  ```bash
  docker-compose logs pulsar
  ```
- Verify that the correct ports are exposed and accessible on your host machine.

## Further Resources

- [Apache Pulsar Documentation](https://pulsar.apache.org/docs/en/standalone/)
- [Pulsar Java Client Documentation](https://pulsar.apache.org/docs/en/client-libraries-java/)

For any additional questions or issues, please refer to the official Apache Pulsar documentation or seek help in the Pulsar community forums.