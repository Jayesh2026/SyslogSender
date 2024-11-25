FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/sysImage-0.0.1-SNAPSHOT.jar sysImage.jar

# Download the OpenTelemetry Java agent
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.2.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

EXPOSE 8081

ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "sysImage.jar"]

# ENTRYPOINT ["java", "-jar", "sisImage.jar"]

# ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar \
#     -Dotel.service.name=your-service-name \
#     -Dotel.traces.exporter=otlp \
#     -Dotel.metrics.exporter=otlp \
#     -Dotel.logs.exporter=otlp \
#     -Dotel.exporter.otlp.endpoint=http://otel-collector:4317"
