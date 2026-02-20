# Tunit 로컬 개발 환경 세팅 가이드

## 1. 사전 준비사항

### 필수 설치 프로그램
- **Docker Desktop** - [다운로드](https://www.docker.com/products/docker-desktop)
- **Java 17** - [다운로드](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Git**

## 2. 프로젝트 클론

```bash
git clone <repository-url>
cd tunit
```

## 3. Docker 서비스 실행

### PostgreSQL + Redpanda 실행
```bash
# Docker Desktop이 실행 중인지 확인
open -a Docker

# 서비스 시작 (백그라운드)
docker-compose up -d

# 서비스 상태 확인
docker-compose ps
```

## 실행 결과 확인
- PostgreSQL: `localhost:5432` (DB: tunit, User: tunit, Password: tunit123)
- Redpanda Kafka: `localhost:9092`
- Redpanda Console: `http://localhost:8081`

## 4. 애플리케이션 실행

```bash
# 방법 1: Gradle로 실행
./gradlew bootRun

# 방법 2: IDE에서 실행
# IntelliJ/Eclipse에서 TunitApplication.java 실행
```

## 5. 개발 환경 확인

- 애플리케이션: `http://localhost:8080`
- H2 Console (사용 안함): -
- Redpanda Console: `http://localhost:8080` (Kafka)

## 6. Docker 서비스 관리

```bash
# 서비스 중지 (데이터 유지)
docker-compose stop

# 서비스 재시작
docker-compose start

# 서비스 완전 종료 (데이터 유지)
docker-compose down

# 서비스 완전 종료 + 데이터 삭제
docker-compose down -v

# 로그 확인
docker-compose logs -f
docker-compose logs -f postgres  # PostgreSQL 로그만
docker-compose logs -f redpanda  # Redpanda 로그만
```

## 7. 데이터베이스 접속 (선택사항)

### IntelliJ DataGrip / DBeaver 설정
- Host: `localhost`
- Port: `5432`
- Database: `tunit`
- User: `tunit`
- Password: `tunit123`

### psql CLI로 접속
```bash
docker exec -it tunit-postgres psql -U tunit -d tunit
```

## 8. 프로파일 전환

### 로컬 개발 (기본값)
```bash
./gradlew bootRun
```

### 운영 환경 (Neon DB)
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## 9. 트러블슈팅

### Docker 컨테이너가 시작되지 않는 경우
```bash
# Docker Desktop 재시작
# 기존 컨테이너 정리
docker-compose down
docker system prune -f
docker-compose up -d
```

### 포트 충돌 시
```bash
# 5432 포트 사용 중인 프로세스 확인
lsof -i :5432
# 해당 프로세스 종료 후 재시작
```

### PostgreSQL 연결 실패 시
```bash
# 컨테이너 상태 확인
docker-compose ps

# PostgreSQL 로그 확인
docker-compose logs postgres

# 컨테이너 재시작
docker-compose restart postgres
```

## 10. 완전 초기화 (처음부터 다시)

```bash
# 모든 컨테이너와 볼륨 삭제
docker-compose down -v

# 다시 시작
docker-compose up -d

# 애플리케이션 재실행
./gradlew clean bootRun
```

---

## 빠른 시작 (원라인 커맨드)

```bash
# 전체 환경 구축 및 실행
docker-compose up -d && sleep 5 && ./gradlew bootRun
```
