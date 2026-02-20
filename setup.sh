#!/bin/bash

# Tunit 개발 환경 자동 세팅 스크립트

set -e

echo "🚀 Tunit 개발 환경 세팅을 시작합니다..."
echo ""

# 1. Docker 실행 확인 및 시작
echo "📦 Docker Desktop 확인 중..."
if ! docker info > /dev/null 2>&1; then
    echo "⚠️  Docker가 실행되지 않았습니다. Docker Desktop을 시작합니다..."
    open -a Docker
    echo "⏳ Docker가 시작될 때까지 15초 대기 중..."
    sleep 15
    
    # Docker가 완전히 시작될 때까지 대기
    while ! docker info > /dev/null 2>&1; do
        echo "⏳ Docker 시작 대기 중..."
        sleep 3
    done
fi
echo "✅ Docker 실행 중"
echo ""

# 2. 기존 컨테이너 정리 (선택사항)
read -p "❓ 기존 Docker 컨테이너를 정리하시겠습니까? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🧹 기존 컨테이너 정리 중..."
    docker-compose down -v 2>/dev/null || true
    echo "✅ 정리 완료"
fi
echo ""

# 3. Docker Compose 서비스 시작
echo "🐘 PostgreSQL과 Redpanda를 시작합니다..."
docker-compose up -d

# 서비스가 준비될 때까지 대기
echo "⏳ 서비스 초기화 대기 중 (10초)..."
sleep 10
echo ""

# 4. 서비스 상태 확인
echo "📊 서비스 상태:"
docker-compose ps
echo ""

# 5. 연결 정보 출력
echo "✅ 개발 환경 세팅 완료!"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📝 서비스 정보"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🐘 PostgreSQL:"
echo "   URL: jdbc:postgresql://localhost:5432/tunit"
echo "   User: tunit"
echo "   Password: tunit123"
echo ""
echo "🔴 Redpanda (Kafka):"
echo "   Bootstrap Server: localhost:9092"
echo "   Console: http://localhost:8081"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 애플리케이션 실행 방법"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  ./gradlew bootRun"
echo ""
echo "또는 IDE에서 TunitApplication.java 실행"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 6. 애플리케이션 실행 여부 확인
read -p "❓ 바로 애플리케이션을 실행하시겠습니까? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🚀 애플리케이션을 시작합니다..."
    ./gradlew bootRun
else
    echo "👋 수동으로 애플리케이션을 실행해주세요: ./gradlew bootRun"
fi
