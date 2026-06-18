# syntax=docker/dockerfile:1

### Build stage ###
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle 래퍼 및 빌드 스크립트 먼저 복사 (의존성 캐시 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# 소스 복사 후 부트 JAR 빌드 (테스트 제외)
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

### Runtime stage ###
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 산출물만 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
