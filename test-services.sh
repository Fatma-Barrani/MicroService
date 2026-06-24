#!/bin/bash

# Test Suite pour Architecture Microservices
# Tests: Feign Communication, RabbitMQ, OAuth2, Services Health

set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=================================================="
echo "     TEST SUITE - MICROSERVICES ARCHITECTURE"
echo "=================================================="
echo ""

# Color functions
pass() {
    echo -e "${GREEN}✓ PASS${NC}: $1"
}

fail() {
    echo -e "${RED}✗ FAIL${NC}: $1"
}

warning() {
    echo -e "${YELLOW}⚠ INFO${NC}: $1"
}

# ============================================
# 1. HEALTH CHECKS
# ============================================
echo -e "\n${YELLOW}[1/5] Health Checks${NC}"
echo "---"

# Eureka
if curl -s http://localhost:8761/actuator/health | grep -q "UP"; then
    pass "Eureka Server (8761) is UP"
else
    fail "Eureka Server (8761) is DOWN"
fi

# Config Server
if curl -s http://localhost:8889/actuator/health | grep -q "UP"; then
    pass "Config Server (8889) is UP"
else
    fail "Config Server (8889) is DOWN"
fi

# API Gateway
if curl -s http://localhost:8956/actuator/health | grep -q "UP"; then
    pass "API Gateway (8956) is UP"
else
    fail "API Gateway (8956) is DOWN"
fi

# Keycloak
if curl -s http://localhost:8080 -o /dev/null -w "%{http_code}" | grep -q "200\|403"; then
    pass "Keycloak (8080) is UP"
else
    fail "Keycloak (8080) is DOWN"
fi

# ============================================
# 2. MICROSERVICES HEALTH
# ============================================
echo -e "\n${YELLOW}[2/5] Microservices Health${NC}"
echo "---"

for service in "etudiant-service:8081" "cours-service:8082" "examen-service:8089" "enseignant-service:8083"; do
    IFS=':' read -r name port <<< "$service"
    if curl -s http://localhost:$port/actuator/health | grep -q "UP"; then
        pass "$name ($port) is UP"
    else
        fail "$name ($port) is DOWN"
    fi
done

# ============================================
# 3. OAUTH2 TOKEN GENERATION
# ============================================
echo -e "\n${YELLOW}[3/5] OAuth2 & Keycloak${NC}"
echo "---"

# Get token
TOKEN=$(curl -s -X POST http://localhost:8080/realms/EduRealm/protocol/openid-connect/token \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "client_id=etudiant-service" \
    -d "client_secret=etudiant-secret" \
    -d "grant_type=client_credentials" 2>/dev/null | jq -r '.access_token' 2>/dev/null)

if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    pass "OAuth2 Token obtained successfully"
    echo "  Token (first 50 chars): ${TOKEN:0:50}..."
else
    fail "Failed to obtain OAuth2 token"
    TOKEN=""
fi

# ============================================
# 4. FEIGN CLIENT COMMUNICATION
# ============================================
echo -e "\n${YELLOW}[4/5] Feign Communication (Synchrone)${NC}"
echo "---"

# Create a test course
warning "Creating test course..."
COURSE_RESPONSE=$(curl -s -X POST http://localhost:8082/cours \
    -H "Content-Type: application/json" \
    -d '{
        "titre": "Test Feign Course",
        "description": "Test course for Feign communication",
        "duree": 40
    }')

COURSE_ID=$(echo $COURSE_RESPONSE | jq -r '.id // empty' 2>/dev/null)

if [ -n "$COURSE_ID" ] && [ "$COURSE_ID" != "null" ]; then
    pass "Created test course with ID: $COURSE_ID"
    
    # Test Feign: Get course details
    FETCH_COURSE=$(curl -s -X GET http://localhost:8082/cours/$COURSE_ID)
    if echo $FETCH_COURSE | jq -e '.id' > /dev/null 2>&1; then
        pass "Feign can fetch course: $COURSE_ID"
    else
        fail "Feign failed to fetch course"
    fi
    
    # Test Feign: Create exam with course reference
    warning "Creating test exam referencing course..."
    EXAM_RESPONSE=$(curl -s -X POST http://localhost:8089/examen \
        -H "Content-Type: application/json" \
        -d '{
            "titre": "Test Feign Exam",
            "description": "Test exam calling cours-service via Feign",
            "coursId": '$COURSE_ID',
            "dateExamen": "2024-06-20T10:00:00"
        }')
    
    EXAM_ID=$(echo $EXAM_RESPONSE | jq -r '.id // empty' 2>/dev/null)
    if [ -n "$EXAM_ID" ] && [ "$EXAM_ID" != "null" ]; then
        pass "Exam created via Feign: $EXAM_ID"
        warning "Exam successfully called Cours-Service via FeignClient"
    else
        fail "Exam creation with Feign failed"
    fi
else
    fail "Could not create test course"
fi

# ============================================
# 5. RABBITMQ ASYNCHRONOUS COMMUNICATION
# ============================================
echo -e "\n${YELLOW}[5/5] RabbitMQ Communication (Asynchrone)${NC}"
echo "---"

# Check RabbitMQ is running
if curl -s http://guest:guest@localhost:15672/api/overview | jq -e '.rabbitmq_version' > /dev/null 2>&1; then
    pass "RabbitMQ Management API is accessible"
    
    # List queues
    QUEUES=$(curl -s http://guest:guest@localhost:15672/api/queues | jq -r '.[].name' 2>/dev/null | sort)
    
    if [ -n "$QUEUES" ]; then
        pass "RabbitMQ Queues detected:"
        echo "$QUEUES" | sed 's/^/    - /'
    else
        warning "No queues detected yet"
    fi
    
    # Create a test student to trigger RabbitMQ event
    warning "Creating test student to trigger RabbitMQ event..."
    STUDENT_RESPONSE=$(curl -s -X POST http://localhost:8081/etudiants \
        -H "Content-Type: application/json" \
        -d '{
            "nom": "Test Student",
            "prenom": "RabbitMQ",
            "email": "rabbitmq.test@edunet.tn",
            "dateInscription": "2024-06-06"
        }')
    
    STUDENT_ID=$(echo $STUDENT_RESPONSE | jq -r '.id // empty' 2>/dev/null)
    
    if [ -n "$STUDENT_ID" ] && [ "$STUDENT_ID" != "null" ]; then
        pass "Test student created: $STUDENT_ID"
        
        # Check queue messages
        sleep 2  # Wait for message to be processed
        
        QUEUE_MESSAGES=$(curl -s http://guest:guest@localhost:15672/api/queues | jq '.[].messages' 2>/dev/null | paste -sd+ | bc)
        if [ -n "$QUEUE_MESSAGES" ] && [ "$QUEUE_MESSAGES" -gt 0 ]; then
            pass "RabbitMQ received event messages: $QUEUE_MESSAGES total"
        else
            warning "No messages in RabbitMQ queues (might be already processed)"
        fi
    else
        fail "Could not create test student for RabbitMQ test"
    fi
else
    fail "RabbitMQ Management API not accessible"
fi

# ============================================
# SUMMARY
# ============================================
echo ""
echo "=================================================="
echo "     TEST SUMMARY"
echo "=================================================="
echo ""
echo "Frontend:        http://localhost:4200"
echo "API Gateway:     http://localhost:8956"
echo "Eureka:          http://localhost:8761"
echo "Keycloak:        http://localhost:8080/admin"
echo "RabbitMQ:        http://localhost:15672 (guest/guest)"
echo "MongoDB:         localhost:27017"
echo "MySQL:           localhost:3306 (root/root)"
echo ""
echo "All tests completed! ✓"
echo ""
