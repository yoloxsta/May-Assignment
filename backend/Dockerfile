# Stage 1: Build Java application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install curl for downloading JDBC driver
RUN apk add --no-cache curl

# Download PostgreSQL JDBC driver
RUN curl -o /app/postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.7.4.jar

# Copy source files
COPY src/main/java/app/*.java ./app/

# Compile Java files with JDBC driver in classpath
RUN javac -cp /app/postgresql.jar -d ./out ./app/*.java

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for downloading JDBC driver in runtime
RUN apk add --no-cache curl

# Download PostgreSQL JDBC driver for runtime
RUN curl -o /app/postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.7.4.jar

# Copy compiled classes from builder
COPY --from=builder /app/out ./out

# Expose port
EXPOSE 8080

# Run the application with JDBC driver in classpath
CMD ["java", "-cp", "./out:/app/postgresql.jar", "app.Main"]
