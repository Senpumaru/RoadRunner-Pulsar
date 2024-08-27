# RoadRunner-Pulsar

This repository contains the Apache Pulsar implementation for the RoadRunner project. It's responsible for handling streaming and response-reply systems.

## Current State

The repository currently includes a basic Pulsar producer implementation. The main class, `PulsarProducer`, is located in `src/main/java/com/roadrunner/PulsarProducer.java`.

### Features

- Connects to a Pulsar cluster
- Creates a producer for a specified topic
- Sends messages to the topic
- Implements `AutoCloseable` for proper resource management

## Prerequisites

- Docker and Docker Compose (for running Pulsar locally)

## Setup

1. Clone the repository:
   ```
   git clone git@github.com:Senpumaru/RoadRunner-Pulsar.git
   cd RoadRunner-Pulsar
   ```

2. Start the Pulsar cluster using Docker Compose:
   ```
   docker compose up --build -d
   ```

## Building the Project

To build the project, run:

```
mvn clean package
```

This command compiles the code, runs tests, and packages the application into a JAR file.

## Running the Application

To run the Pulsar producer, use the following Maven command:

```
mvn exec:java -Dexec.mainClass="com.roadrunner.PulsarProducer"
```

This command will execute the `main` method in the `PulsarProducer` class, which sends a "Hello, Pulsar!" message to the "my-topic" topic.

## Configuration

The Pulsar producer is currently configured to connect to `pulsar://pulsar:6650`. If you're running Pulsar on a different address, update the service URL in the `PulsarProducer` class.

## Next Steps

- Implement a Pulsar consumer to read messages from topics
- Add more complex message processing logic
- Implement error handling and retry mechanisms
- Create integration tests

## Contributing

Please read `CONTRIBUTING.md` for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the [insert your license here] - see the `LICENSE.md` file for details.