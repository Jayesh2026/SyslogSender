# Syslog Sender Application  

A Spring Boot application that sends Syslog messages to a configured Syslog server. This project demonstrates how to integrate with a Syslog server using the `syslog-java-client` library and provides an easy-to-use REST endpoint for sending test Syslog messages.  

---

## Features  
- Sends Syslog messages to a server using the **RFC 5424** protocol.  
- Configurable hostname and port for the Syslog server.  
- REST endpoint for testing Syslog message sending (`/send-syslog`).  
- Supports local and Dockerized environments.  
- Includes OpenTelemetry Collector configuration for advanced logging and observability.  

---

## Prerequisites  
- **JDK 21** (Configured in `build.gradle`)  
- **Gradle 8+**  
- Docker (optional, for containerized deployment)  

---

## Installation  

1. **Clone the repository**:  
   Clone the repository to your local machine.  
   ```bash
   git clone <repository-url>
   cd syslog-sender

2. **Build the project:**
   Build the project using Gradle. This will compile the application and generate the necessary files.
  ```bash
  ./gradlew build
  ```
3. **Configure application properties:**
    Update the following properties in ```application.properties``` based on your environment:
    - For local setup, uncomment and use localhost.
    - For Docker setup, use otel-collector as the Syslog server hostname.
   ```bash
   syslog.server.port=514
    # Uncomment for local setup:
    # syslog.server.hostname=localhost
    # Uncomment for Docker setup:
   syslog.server.hostname=otel-collector
   ```
