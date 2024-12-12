
# Syslog Sender Spring Boot Application

## Overview
This Spring Boot application demonstrates how to send Syslog messages using the **CloudBees Syslog Java Client library**. It also integrates with the **OpenTelemetry Collector** as a Syslog receiver to collect and process Syslog messages for observability and monitoring.

---

## 1. What is Syslog?
**Syslog** (System Logging Protocol) is a standard protocol used for message logging. It allows devices and applications to send logs or events to a central server for monitoring and analysis. Syslog is commonly used for:

- System diagnostics and troubleshooting.
- Centralized log management.
- Application performance monitoring.

---

## 2. Syslog Message Types

Syslog messages are categorized using **Facility** and **Severity** values:  

### i) Facility  
The **Facility** value identifies the type of system or process generating the log message. It ranges from **0** to **23**, with predefined categories for **0–15** and locally defined categories for **16–23**.

#### Common Facilities:
| **Facility** | **Code** | **Description**                     |
|--------------|----------|-------------------------------------|
| `kern`       | 0        | Kernel messages                    |
| `user`       | 1        | User-level messages                |
| `mail`       | 2        | Mail system messages               |
| `daemon`     | 3        | System daemons                     |
| `auth`       | 4        | Security/authentication messages   |
| `syslog`     | 5        | Syslog internal messages           |

---

### ii) Severity  
The **Severity** value indicates the importance or urgency of the message. It is represented as a numerical code ranging from **0** (most critical) to **7** (least critical).  

#### Severity Levels:
| **Severity**     | **Code** | **Description**                           |
|-------------------|----------|------------------------------------------|
| **Emergency**     | 0        | System is unusable                      |
| **Alert**         | 1        | Action must be taken immediately        |
| **Critical**      | 2        | Critical conditions                     |
| **Error**         | 3        | Error conditions                        |
| **Warning**       | 4        | Warning conditions                      |
| **Notice**        | 5        | Normal but significant conditions       |
| **Informational** | 6        | Informational messages                  |
| **Debug**         | 7        | Debug-level messages                    |

---

## 3. Message Format (RFC 5424)
Syslog messages adhere to the RFC 5424 format, consisting of:

### Header Structure
```plaintext
<PRI>VERSION TIMESTAMP HOSTNAME APP-NAME PROCID MSGID
```
- `PRI` : Priority value (Facility * 8 + Severity)
- `VERSION`: Protocol version (1)
- `TIMESTAMP`: ISO 8601 formatted timestamp
- `HOSTNAME`: Machine name
- `APP-NAME`: Application name
- `PROCID`: Process identifier
- `MSGID`: Message identifier

### Structured Data
- Contains structured information in key-value pairs
- Format: [SD-ID param-name="param-value"]
- Multiple structured data elements can be present

### Message Body
- UTF-8 encoded message content
- Can contain any valid Unicode characters

### For example:
```plaintext
<14>1 2024-12-12T12:34:56Z myhostname syslogSender 1234 - - Test message
```

In this message:
- `<14>`: Priority (Facility `1` for **user** and Severity `6` for **Informational**).  
- `1`: Version of the Syslog protocol.  
- `2024-12-12T12:34:56Z`: Timestamp.  
- `myhostname`: Hostname of the system generating the message.  
- `syslogSender`: Application name.  
- `1234`: Process ID (optional).  
- `Test message`: Actual message content.  

## 4. OpenTelemetry Collector as a Syslog Receiver

### OpenTelemetry Collector Configuration
The OpenTelemetry Collector is configured to receive and process Syslog messages in **RFC 5424** format.

Key configuration in `otel-collector-config.yaml`:
```yaml
receivers:
  syslog:
    tcp:
      listen_address: "0.0.0.0:514"  # Listening on TCP port 514 for Syslog
    protocol: rfc5424

exporters:
  debug:
    verbosity: detailed

service:
  pipelines:
    logs:
      receivers: [syslog]   # Collect Syslog logs
      exporters: [debug]    # Send to debug exporter
```

---

## 5. Syslog Client Implementation

### Code Snippets from the Application

To send Syslog messages, this application uses the CloudBees Syslog Java Client library. Add the following dependency to your build.gradle file:

build.gradle
```
implementation 'com.cloudbees:syslog-java-client:1.1.7'
```
This will allow your Spring Boot application to send Syslog messages using the RFC 5424 format.

#### Define Syslog Host and Port
In `application.properties`, the Syslog server configuration is defined:
```properties
syslog.server.hostname=${SYSLOG_SERVER_HOSTNAME:myhostname}
syslog.server.port=${SYSLOG_SERVER_PORT:514}
```

#### Sending Syslog Messages
The service class `SyslogSenderServiceImpl` uses **CloudBees Syslog Java Client** to send Syslog messages:
```java
messageSender.setSyslogServerHostname(syslogServerHostname);
messageSender.setSyslogServerPort(syslogServerPort);
messageSender.setDefaultFacility(Facility.USER);
messageSender.setDefaultSeverity(Severity.INFORMATIONAL);
messageSender.setMessageFormat(MessageFormat.RFC_5424);
```

Example method to send messages:
```java
public void sendSyslogMessage(String message, Severity severity) {
    try {
        messageSender.setDefaultSeverity(severity);
        messageSender.sendMessage(message);
        logger.info("Syslog message sent: {}", message);
    } catch (Exception e) {
        logger.error("Failed to send syslog message: {}", e.getMessage(), e);
    }
}
```

#### Testing the Syslog Message
Use the endpoint `/send-syslog` to test:
```bash
curl -X POST "http://localhost:8081/send-syslog?message=TestMessage&severity=INFORMATIONAL"
```

---

## 6. Docker Integration

### Dockerfile
The Dockerfile includes the OpenTelemetry Java Agent for tracing:
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY build/libs/syslogSender-0.0.1-SNAPSHOT.jar syslogSender.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.2.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "syslogSender.jar"]
```

### Docker Compose
The application is run with the OpenTelemetry Collector via Docker Compose:
```yaml
services:
  syslog-sender:
    build:
      context: .
    ports:
      - "8081:8081"
    environment:
      SYSLOG_SERVER_HOSTNAME: myhostname
      SYSLOG_SERVER_PORT: 514
```

---

## 7. Running the Application
1. Build the application:
   ```bash
   ./gradlew build
   ```
2. Start the application with Docker Compose:
   ```bash
   docker-compose up --build
   ```
3. Test sending Syslog messages:
   ```bash
   curl -X POST "http://localhost:8081/send-syslog?message=HelloSyslog&severity=INFORMATIONAL"
   ```

### Output Message after Sending Syslog

When you hit the test Syslog message URL with the given `curl` command, the application will log the following messages in the **application logs**:

#### Example Log Output:
```plaintext
INFO  2024-12-12 14:32:45 [SyslogSenderServiceImpl]: Syslog message sent: HelloSyslog
```

At the **Syslog receiver (OpenTelemetry Collector)**, you will observe an output similar to this:
```plaintext
<14>1 2024-12-12T14:32:45Z myhostname syslogSender 1234 - - HelloSyslog
```

Here:
- `<14>`: The calculated priority value (Facility `USER` and Severity `INFORMATIONAL`).
- `1`: Protocol version.
- `2024-12-12T14:32:45Z`: Timestamp.
- `myhostname`: The host sending the message.
- `syslogSender`: Application name.
- `1234`: Process ID (can be a placeholder).
- `HelloSyslog`: The actual message.

---

### Sending Syslog with Different Severities

You can modify the severity by passing it as a query parameter when testing the Syslog message.

#### Example: Sending a DEBUG Message
```bash
curl -X POST "http://localhost:8081/send-syslog?message=DebugMessage&severity=DEBUG"
```

**Output:**
```plaintext
INFO  2024-12-12 14:35:12 [SyslogSenderServiceImpl]: Syslog message sent: DebugMessage
<7>1 2024-12-12T14:35:12Z myhostname syslogSender 1234 - - DebugMessage
```
Here, `<7>` indicates the **DEBUG** severity level.

#### Example: Sending an ERROR Message
```bash
curl -X POST "http://localhost:8081/send-syslog?message=ErrorMessage&severity=ERROR"
```

**Output:**
```plaintext
INFO  2024-12-12 14:36:28 [SyslogSenderServiceImpl]: Syslog message sent: ErrorMessage
<3>1 2024-12-12T14:36:28Z myhostname syslogSender 1234 - - ErrorMessage
```
Here, `<3>` indicates the **ERROR** severity level.

---

### Notes
- You can replace the OpenTelemetry Collector configuration or use a different exporter (e.g., Elasticsearch or Splunk) based on your requirements.
- Ensure the Syslog server is reachable at the hostname and port specified.



