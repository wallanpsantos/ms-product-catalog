# ── Stage 1: Builder ─────────────────────────────────────────────
FROM ghcr.io/graalvm/native-image-community:21.0.2 AS builder

WORKDIR /app

# Dependency descriptors first — this layer is cached until build files change.
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle

RUN chmod +x gradlew && \
    ./gradlew dependencies --no-daemon --quiet

COPY src ./src

# Tests run in CI — skip here to keep the build fast.
RUN ./gradlew nativeCompile --no-daemon -x test


# ── Stage 2: Runtime ─────────────────────────────────────────────
# Minimal Alpine base: ~8 MB, no JDK, no build tools.
FROM alpine:3.21.3

# Values injected at build time from application.yml via compose build.args.
# Defaults mirror application.yml so a plain `docker build .` still works.
ARG APP_NAME=ms-product-catalog
ARG APP_VERSION=1.0.0
ARG SERVER_PORT=8080
ARG SPRING_PROFILE=dev

LABEL org.opencontainers.image.title="${APP_NAME}" \
      org.opencontainers.image.description="Product Catalog Microservice — GraalVM Native" \
      org.opencontainers.image.version="${APP_VERSION}" \
      org.opencontainers.image.vendor="Wallan" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.source="https://github.com/wallanpsantos/${APP_NAME}"

# Only non-sensitive tuning values belong here.
# Secrets and URIs must be injected at runtime via -e or a secrets manager.
ENV SERVER_PORT=${SERVER_PORT} \
    SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}

# Single RUN layer: install curl for healthcheck, then create a non-root user.
RUN apk add --no-cache curl && \
    addgroup -S appgroup && \
    adduser  -S appuser -G appgroup

WORKDIR /app

# --chown applies ownership at copy time, avoiding a separate chown layer.
COPY --from=builder \
     --chown=appuser:appgroup \
     /app/build/native/nativeCompile/${APP_NAME} \
     ./${APP_NAME}

USER appuser

EXPOSE ${SERVER_PORT}

# A GraalVM native binary starts in ~50–200 ms, so 10 s start-period is enough.
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl --fail --silent http://localhost:${SERVER_PORT}/actuator/health || exit 1

ENTRYPOINT ["/app/${APP_NAME}"]