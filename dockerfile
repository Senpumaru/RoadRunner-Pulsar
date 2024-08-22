FROM openjdk:17-slim

# Install essential tools
RUN apt-get update && apt-get install -y \
    curl \
    git \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Set up Maven
ENV MAVEN_HOME /usr/share/maven

# Set up workspace
WORKDIR /app

# Keep the container running
CMD ["tail", "-f", "/dev/null"]