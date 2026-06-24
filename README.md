# 📚 Architecture Microservices - Plateforme Éducative

## 📋 Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Prérequis](#prérequis)
4. [Installation](#installation)
5. [Configuration Keycloak](#configuration-keycloak)
6. [Accès aux services](#accès-aux-services)
7. [Communication Synchrone (Feign)](#communication-synchrone-feign)
8. [Communication Asynchrone (RabbitMQ)](#communication-asynchrone-rabbitmq)
9. [Sécurité OAuth2](#sécurité-oauth2)
10. [Valeurs ajoutées](#valeurs-ajoutées)
11. [Tests et validation](#tests-et-validation)
12. [Grille d'évaluation](#grille-dévaluation)
13. [Dépannage](#dépannage)

---

## 🎯 Vue d'ensemble

Plateforme éducative distribuée basée sur une architecture microservices avec :

- **4 Microservices Spring Boot** pour la gestion des étudiants, cours, enseignants et examens
- **Backend NestJS** pour les inscriptions
- **Frontend Angular** pour l'interface utilisateur
- **Eureka** pour la découverte de services
- **Config Server** pour la gestion centralisée des configurations
- **API Gateway** (Spring Cloud Gateway) pour le routage
- **Keycloak** pour l'authentification et autorisation OAuth2
- **RabbitMQ** pour la communication asynchrone
- **Docker Compose** pour l'orchestration complète

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT / FRONTEND (4200)                 │
│                        Angular App                           │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│              KEYCLOAK (8080) - OAuth2 / OpenID              │
│           Authentification & Gestion des droits             │
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│           API GATEWAY (8956) - Spring Cloud Gateway         │
│         Routage, Load Balancing, Sécurité                   │
└─────────────────────────────────────────────────────────────┘
          │                     │                      │
          ▼                     ▼                      ▼
    ┌─────────────┐      ┌─────────────┐      ┌──────────────┐
    │ EUREKA      │      │ CONFIG      │      │   SERVICES   │
    │ (8761)      │      │ SERVER      │      │  (8081-8089) │
    │             │      │ (8889)      │      │              │
    └─────────────┘      └─────────────┘      └──────────────┘
                                                      │
                    ┌─────────────────────────────────┼─────────────────────────────────┐
                    │                                 │                                 │
                    ▼                                 ▼                                 ▼
          ┌──────────────────┐          ┌──────────────────┐          ┌──────────────────┐
          │ ETUDIANT (8081)  │          │  COURS (8082)    │          │ EXAMEN (8089)    │
          │ - Feignant: Cours│          │ - Feignant:      │          │ - Feignant:      │
          │ - Feignant:      │          │   Enseignant     │          │   Etudiant       │
          │   Examen         │          │                  │          │   Enseignant     │
          └──────────────────┘          └──────────────────┘          └──────────────────┘
                    │                                 │                         │
                    └─────────────┬───────────────────┴─────────────┬───────────┘
                                  │                                 │
                                  ▼                                 ▼
                          ┌─────────────────┐          ┌─────────────────────┐
                          │    RABBITMQ     │          │    MySQL (3306)     │
                          │   (5672/15672)  │          │   - etudiant_db     │
                          │   Async Events  │          │   - cours_db        │
                          └─────────────────┘          │   - exam_db         │
                                  │                    └─────────────────────┘
                                  ▼
                    ┌──────────────────────────┐
                    │ INSCRIPTION (3000)       │
                    │ NestJS Backend           │
                    └──────────────────────────┘
                                  │
                                  ▼
                    ┌──────────────────────────┐
                    │   MongoDB (27017)        │
                    │   - auth_db              │
                    └──────────────────────────┘
```

---

## 📦 Prérequis

- **Docker** (v20.10+) et **Docker Compose** (v1.29+)
- **Java 17** (si compilation locale)
- **Maven 3.8+** (si compilation locale)
- **Node.js 18+** (si compilation frontend locale)
- **Git**

**Vérifier les versions :**
```bash
docker --version
docker-compose --version
java -version
```

---

## 🚀 Installation

### 1. Cloner le repository
```bash
git clone <votre-repo>
cd MicroService
```

### 2. Démarrer tous les conteneurs
```bash
docker-compose up -d
```

**Suivi du démarrage :**
```bash
docker-compose logs -f
```

### 3. Vérifier que tous les services sont UP
```bash
docker ps
```

Vous devriez voir 13 conteneurs en UP :
- config-server
- eureka-server
- api-gateway
- etudiant-service
- cours-service
- examen-service
- enseignant-service
- frontend
- nest-backend
- keycloak-db
- keycloak
- mysql-db
- rabbitmq
- mongo-db

### 4. Attendre la disponibilité des services (2-3 minutes)
```bash
# Vérifier Eureka
curl http://localhost:8761

# Vérifier Keycloak
curl http://localhost:8080

# Vérifier API Gateway
curl http://localhost:8956/actuator/health
```

---

## 🔐 Configuration Keycloak

### Accès à Keycloak
- **URL** : http://localhost:8080
- **Admin Console** : http://localhost:8080/admin
- **Username** : `admin`
- **Password** : `admin`

### ✅ Le realm "EduRealm" est auto-importé avec :

**Utilisateurs prédéfinis :**
| Username | Password | Rôle | Email |
|----------|----------|------|-------|
| admin | admin123 | admin | admin@edunet.tn |
| etudiant1 | password123 | student | etudiant1@edunet.tn |
| enseignant1 | password123 | teacher | enseignant1@edunet.tn |

**Clients OAuth2 :**
- `etudiant-service` (secret: etudiant-secret)
- `cours-service` (secret: cours-secret)
- `examen-service` (secret: examen-secret)
- `enseignant-service` (secret: enseignant-secret)
- `api-gateway` (secret: gateway-secret)
- `frontend` (public client - pas de secret)

### 🔧 Si vous devez ajouter un utilisateur manuellement :

1. Allez à http://localhost:8080/admin
2. Connectez-vous avec `admin / admin123`
3. Sélectionnez le realm **EduRealm**
4. Menu **Users** → **Add user**
5. Remplissez les informations
6. Credentials → Set Password → Désactivez "Temporary"
7. Role Mapping → Assignez des rôles

---

## 📍 Accès aux services

### Dashboard & Monitoring

| Service | URL | Accès |
|---------|-----|-------|
| **Eureka** | http://localhost:8761 | Découverte services |
| **Keycloak Admin** | http://localhost:8080/admin | admin / admin123 |
| **Config Server** | http://localhost:8889 | Configurations |
| **RabbitMQ Management** | http://localhost:15672 | guest / guest |
| **Frontend** | http://localhost:4200 | Angular App |

### API Services

| Service | URL | Port | BD | Authentication |
|---------|-----|------|----|----|
| **Etudiant** | http://localhost:8081 | 8081 | MySQL | OAuth2 |
| **Cours** | http://localhost:8082 | 8082 | MySQL | OAuth2 |
| **Examen** | http://localhost:8089 | 8089 | MySQL | OAuth2 |
| **Enseignant** | http://localhost:8083 | 8083 | MySQL | OAuth2 |
| **API Gateway** | http://localhost:8956 | 8956 | - | OAuth2 |
| **Inscription (NestJS)** | http://localhost:3000 | 3000 | MongoDB | Local |

### Accès par l'API Gateway (Recommandé)
```bash
# Liste les étudiants via la Gateway
curl -X GET http://localhost:8956/etudiants

# Via Keycloak (avec token)
curl -X GET http://localhost:8956/etudiants \
  -H "Authorization: Bearer <token>"
```

---

## 🔄 Communication Synchrone (Feign)

### Architecture

Les microservices utilisent **Feign Clients** (OpenFeign) pour la communication synchrone :

```
Etudiant-Service ──(Feign)──→ Cours-Service
     ↓
Examen-Service ──(Feign)──→ Etudiant-Service
     ↓
Examen-Service ──(Feign)──→ Enseignant-Service

Cours-Service ──(Feign)──→ Enseignant-Service
```

### Clients Feign Configurés

**1. CoursClient (dans Etudiant-Service)**
```java
@FeignClient(name = "cours", url = "http://localhost:8082")
public interface CoursClient {
    @GetMapping("/cours")
    List<Cours> getAllCours();
    
    @GetMapping("/cours/{id}")
    Cours getCoursById(@PathVariable Long id);
}
```

**2. EtudiantClient (dans Examen-Service)**
```java
@FeignClient(name = "ETUDIANT-SERVICE")
public interface EtudiantClient {
    @GetMapping("/etudiants/{id}")
    Etudiant getEtudiantById(@PathVariable Long id);
}
```

**3. EnseignantClient (dans Cours-Service)**
```java
@FeignClient(name = "MicroServiceProject")
public interface EnseignantClient {
    @GetMapping("/enseignants/{id}")
    Enseignant getEnseignantById(@PathVariable Long id);
}
```

### 🧪 Test Feign : Créer un examen avec liaison

**Scenario :** Créer un examen pour un cours existant

```bash
# 1. Créer un cours
curl -X POST http://localhost:8082/cours \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Test Feign",
    "description": "Test communication Feign"
  }'

# Response: { "id": 1, "titre": "Test Feign", ... }

# 2. Créer un examen qui charge le cours via Feign
curl -X POST http://localhost:8089/examen \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Examen Feign",
    "coursId": 1,
    "date": "2024-06-15"
  }'

# 3. Vérifier que le cours est chargé par Feign
curl -X GET http://localhost:8089/examen/1
```

**Vérification en logs :**
```bash
docker-compose logs examen-service | grep "FeignClient"
```

---

## 🔄 Communication Asynchrone (RabbitMQ)

### Architecture des Queues

```
Etudiant-Service ──(publish)──→ RabbitMQ ──(subscribe)──→ Examen-Service
Cours-Service ───(publish)──→ RabbitMQ ──(subscribe)──→ Examen-Service
```

### Queues Configurées

| Queue | Producteur | Consommateur | Événement |
|-------|-----------|---|----------|
| `etudiant_events` | Etudiant-Service | Examen-Service | Création/Modification étudiant |
| `cours_events` | Cours-Service | Examen-Service | Création cours |
| `assign_examen_queue` | Examen-Service | Etudiant-Service | Assignation examen |

### Configuration RabbitMQ

**RabbitMQ Admin :** http://localhost:15672 (guest/guest)

Queues visibles dans Admin → Queues :
- `etudiant_events` - Durable, Auto-delete: No
- `cours_events` - Durable, Auto-delete: No
- `assign_examen_queue` - Durable, Auto-delete: No

### 🧪 Test RabbitMQ : Créer un étudiant et recevoir l'événement

**Scenario :** Créer un étudiant → RabbitMQ notifie Examen-Service

```bash
# 1. Créer un étudiant via API
curl -X POST http://localhost:8081/etudiants \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Ahmed Ben Ali",
    "email": "ahmed@example.com",
    "prenom": "Ahmed"
  }'

# Response: { "id": 1, "nom": "Ahmed Ben Ali", ... }

# 2. Vérifier que le message est entré dans la queue
# Admin RabbitMQ : http://localhost:15672
# Menu: Queues → etudiant_events → Messages

# 3. Consulter les logs pour voir le message traité
docker-compose logs examen-service | grep "RabbitListener"
```

**Message publié dans etudiant_events :**
```json
{
  "event": "ETUDIANT_CREATED",
  "etudiantId": 1,
  "nom": "Ahmed Ben Ali",
  "timestamp": "2024-06-06T10:30:00Z"
}
```

**Consumer dans Examen-Service :**
```java
@RabbitListener(queues = RabbitMQConfig.ETUDIANT_QUEUE)
public void handleEtudiantEvent(EtudiantEvent event) {
    log.info("Event reçu: {}", event);
    // Logique métier (ex: mettre à jour les examens)
}
```

### Vérifier les messages en temps réel

```bash
# Console RabbitMQ
# http://localhost:15672 → Admin → Connections/Channels

# Ou via Docker
docker exec -it rabbitmq rabbitmqctl list_queues
docker exec -it rabbitmq rabbitmqctl list_connections
```

---

## 🔐 Sécurité OAuth2

### Configuration OAuth2

Les microservices sont configurés comme **Resource Servers** avec Keycloak.

**Dépendance Maven (déjà présente) :**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

**application.properties :**
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://keycloak:8080/realms/EduRealm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://keycloak:8080/realms/EduRealm/protocol/openid-connect/certs
```

### 🧪 Test OAuth2 : Obtenir un token et accéder aux services

**Scenario :** Authentification et appel API sécurisé

```bash
# 1. Obtenir un token d'accès (Client Credentials Flow)
TOKEN=$(curl -X POST http://localhost:8080/realms/EduRealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=etudiant-service" \
  -d "client_secret=etudiant-secret" \
  -d "grant_type=client_credentials" \
  | jq -r '.access_token')

echo "Token: $TOKEN"

# 2. Utiliser le token pour appeler un service sécurisé
curl -X GET http://localhost:8081/etudiants \
  -H "Authorization: Bearer $TOKEN"

# 3. Appel via API Gateway
curl -X GET http://localhost:8956/etudiants \
  -H "Authorization: Bearer $TOKEN"
```

**Ou avec Resource Owner Password Flow (pour l'utilisateur) :**
```bash
TOKEN=$(curl -X POST http://localhost:8080/realms/EduRealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=frontend" \
  -d "grant_type=password" \
  -d "username=etudiant1" \
  -d "password=password123" \
  | jq -r '.access_token')

curl -X GET http://localhost:8081/etudiants \
  -H "Authorization: Bearer $TOKEN"
```

### Vérifier le contenu du token JWT
```bash
# Décoder le token (sans vérification de signature)
echo $TOKEN | cut -d'.' -f2 | base64 -d | jq .
```

---

## ✨ Valeurs ajoutées

### 1️⃣ Swagger/OpenAPI Centralisé (API Gateway)

**Implémentation :**
```xml
<!-- pom.xml ApiGateway -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.4</version>
</dependency>
```

**Configuration :**
```yaml
# application.yml
springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    urls:
      - name: Etudiant Service
        url: /etudiants/v3/api-docs
      - name: Cours Service
        url: /cours/v3/api-docs
      - name: Examen Service
        url: /examen/v3/api-docs
```

**Accès :**
- http://localhost:8956/swagger-ui.html

### 2️⃣ Monitoring Prometheus + Grafana

**Docker Compose addition :**
```yaml
prometheus:
  image: prom/prometheus
  ports:
    - "9090:9090"
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml

grafana:
  image: grafana/grafana
  ports:
    - "3000:3000"  # Note: Change port si Inscription utilise 3000
  depends_on:
    - prometheus
```

**Metrics actuator (déjà actifs) :**
- http://localhost:8081/actuator/metrics
- http://localhost:8082/actuator/metrics

### 3️⃣ CI/CD avec GitHub Actions

**Fichier `.github/workflows/build.yml` :**
```yaml
name: Build & Test

on:
  push:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up Java
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean package
      - name: Build Docker images
        run: docker-compose build
      - name: Test services
        run: docker-compose up -d && sleep 30 && ./test-services.sh
```

### 4️⃣ Logs centralisés avec ELK Stack

**Docker Compose addition :**
```yaml
elasticsearch:
  image: docker.elastic.co/elasticsearch/elasticsearch:8.0.0
  ports:
    - "9200:9200"

logstash:
  image: docker.elastic.co/logstash/logstash:8.0.0
  volumes:
    - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf

kibana:
  image: docker.elastic.co/kibana/kibana:8.0.0
  ports:
    - "5601:5601"
```

### 5️⃣ Déploiement Kubernetes

**kubernetes/deployment.yaml :**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: etudiant-service
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: etudiant-service
        image: fatmabarrani/etudiant-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: kubernetes
```

---

## 🧪 Tests et validation

### 1. Tester tous les services

```bash
#!/bin/bash
# test-services.sh

echo "=== Eureka ===" 
curl -s http://localhost:8761/eureka/apps | grep -o '"status">.*<' | head -5

echo "\n=== API Gateway Health ==="
curl -s http://localhost:8956/actuator/health | jq .

echo "\n=== Etudiant Service ==="
curl -s http://localhost:8081/actuator/health | jq .

echo "\n=== Keycloak ==="
curl -s http://localhost:8080 -o /dev/null -w "HTTP %{http_code}\n"

echo "\n=== RabbitMQ ==="
curl -s http://guest:guest@localhost:15672/api/queues | jq '.[] | .name'

echo "\n=== MongoDB ==="
docker exec mongo-db mongo --eval "db.adminCommand('ping')" --quiet
```

### 2. Checklist d'évaluation (Grille 20 pts)

#### ✅ Microservices Spring Boot (1 pt)
```bash
curl -X GET http://localhost:8081/actuator/health | jq '.status'
curl -X GET http://localhost:8082/actuator/health | jq '.status'
curl -X GET http://localhost:8089/actuator/health | jq '.status'
curl -X GET http://localhost:8083/actuator/health | jq '.status'
# Expect: "UP" pour tous
```

#### ✅ Compréhension du code (3 pts)
- Lire et expliquer les services (Etudiant/Cours/Examen/Enseignant)
- Architecture des entities, repositories, controllers
- Patterns utilisés (DTO, mappers, services)

#### ✅ Technologie avancée + BD (2 pts)
```bash
# MySQL
docker exec mysql-db mysql -uroot -proot -e "SHOW DATABASES;" | grep etudiant_db

# MongoDB
docker exec mongo-db mongo --eval "db.auth_db.getCollectionNames()" 
```

#### ✅ Eureka (1 pt)
```bash
# Accès Eureka UI
open http://localhost:8761
# Vérifier que tous les services sont enregistrés (DOWN/UP)
```

#### ✅ Config Server (1 pt)
```bash
curl -s http://localhost:8889/ETUDIANT-SERVICE/default | jq .
```

#### ✅ API Gateway (1 pt)
```bash
# Router via Gateway
curl http://localhost:8956/etudiants
curl http://localhost:8956/cours
```

#### ✅ Sécurité Keycloak (2.5 pts)
```bash
# Keycloak UI
open http://localhost:8080/admin
# Realm: EduRealm ✓
# Utilisateurs: admin, etudiant1, enseignant1 ✓
# Clients OAuth2 configurés ✓

# Token + appel sécurisé
TOKEN=$(curl -s -X POST http://localhost:8080/realms/EduRealm/protocol/openid-connect/token \
  -d "client_id=etudiant-service&client_secret=etudiant-secret&grant_type=client_credentials" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  | jq -r '.access_token')

curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/etudiants
```

#### ✅ Git & Documentation (1 pt)
```bash
# Commits réguliers
git log --oneline | head -20

# README.md présent et complet
test -f README.md && echo "README.md existe"
```

#### ✅ Docker Compose (2 pts)
```bash
docker-compose ps
# Doit afficher 13 services UP
```

#### ✅ Frontend (1.5 pt)
```bash
open http://localhost:4200
# Vérifier que le frontend appelle http://localhost:8956
# Inspect Network tab: voir les requêtes vers /etudiants, /cours, etc.
```

#### ✅ Communication Synchrone Feign (1 pt)
```bash
# Créer un examen avec coursId et vérifier le chargement Feign
curl -X POST http://localhost:8089/examen \
  -H "Content-Type: application/json" \
  -d '{"titre":"Test","coursId":1}'

docker-compose logs examen-service | grep CoursClient
```

#### ✅ Communication Asynchrone RabbitMQ (1 pt)
```bash
# Créer un étudiant et vérifier le message RabbitMQ
curl -X POST http://localhost:8081/etudiants \
  -d '{"nom":"Test","email":"test@test.com"}'

# Vérifier queue
docker exec rabbitmq rabbitmqctl list_queues
```

#### ✅ Valeurs ajoutées (2 pts)
- ✅ OAuth2 + Keycloak (2.5 pts couverts par sécurité)
- ⚠️ À implémenter : 2 parmi Swagger, Prometheus+Grafana, CI/CD, Kubernetes

---

## 🔍 Grille d'évaluation - Points actuels

| Critère | Points | Statut | Actions |
|---------|--------|--------|---------|
| Microservices Spring Boot | 1 | ✅ OK | Rien |
| Compréhension code | 3 | ✅ OK | Présentation |
| Tech avancée + BD | 2 | ✅ OK (MySQL + MongoDB) | Rien |
| Eureka Server | 1 | ✅ OK | Rien |
| Config Server | 1 | ✅ OK | Rien |
| API Gateway | 1 | ✅ OK | Rien |
| **Keycloak OAuth2** | **2.5** | **✅ NEW!** | Redémarrer avec docker-compose |
| Git & Documentation | 1 | ⚠️ À améliorer | Créer README + commits |
| Docker Compose | 2 | ✅ OK | Rien |
| Frontend | 1.5 | ✅ OK | Vérifier routing |
| Feign (Synchrone) | 1 | ⚠️ À tester | Test intégration |
| RabbitMQ (Asynchrone) | 1 | ⚠️ À tester | Test intégration |
| **Valeurs ajoutées** | **2** | ⚠️ À implémenter | Swagger + Prometheus |
| **TOTAL** | **20** | **~15.5** | **À compléter** |

---

## 📋 Plan d'action pour 19/20 (en 1/2 journée)

### Phase 1 : Déploiement Keycloak (10 min)
```bash
docker-compose up -d
# Attendre 2 min que Keycloak démarre
curl http://localhost:8080/admin  # Vérifier accès
```

### Phase 2 : Tests Feign & RabbitMQ (20 min)
```bash
# Test Feign
curl -X POST http://localhost:8089/examen \
  -H "Content-Type: application/json" \
  -d '{"titre":"Test Feign","coursId":1}'

# Test RabbitMQ  
curl -X POST http://localhost:8081/etudiants \
  -H "Content-Type: application/json" \
  -d '{"nom":"Test","email":"test@test.com"}'
```

### Phase 3 : Ajouter Swagger (15 min)
```bash
# Ajouter dépendance dans ApiGateway/pom.xml
# Configurer springdoc dans application.properties
# Accès: http://localhost:8956/swagger-ui.html
```

### Phase 4 : Ajouter Prometheus + Grafana (15 min)
```bash
# Ajouter services dans docker-compose.yml
# Configurer prometheus.yml
# Dashboard Grafana: http://localhost:3001
```

### Phase 5 : Commits Git + README (10 min)
```bash
git add .
git commit -m "feat: add Keycloak OAuth2 security"
git commit -m "feat: add Swagger documentation"
git commit -m "feat: add Prometheus+Grafana monitoring"
git commit -m "docs: add comprehensive README"
git push
```

### Résultat final : **19/20** ✅
- ✅ Keycloak + OAuth2 (2.5 pts)
- ✅ Feign testé (1 pt)
- ✅ RabbitMQ testé (1 pt)
- ✅ Swagger (1.5 pts valeurs ajoutées)
- ✅ Prometheus+Grafana (0.5 pts valeurs ajoutées)
- ✅ README + Git (1 pt)
- ❌ -1 pt : Point bonus Kubernetes

---

## 🐛 Dépannage

### Services n'apparaissent pas dans Eureka
```bash
# Vérifier les logs
docker-compose logs eureka-server

# Redémarrer tous les services
docker-compose restart
```

### Keycloak ne démarre pas
```bash
# Vérifier l'état
docker logs keycloak

# Reconstruire
docker-compose down keycloak keycloak-db
docker-compose up -d keycloak
```

### RabbitMQ vide
```bash
# Purger et recréer les queues
docker exec rabbitmq rabbitmqctl reset
docker-compose restart etudiant-service
```

### Frontend ne contacte pas l'API
```bash
# Vérifier la configuration frontend
cat frontend/src/environments/environment.ts

# Checker CORS dans application.properties
curl -i http://localhost:8956/etudiants
```

### Erreur JWT/Token invalide
```bash
# Redémarrer Keycloak pour recréer les clés
docker-compose down keycloak
docker-compose up -d keycloak

# Attendre 30 sec et obtenir nouveau token
TOKEN=$(curl -s -X POST http://localhost:8080/realms/EduRealm/protocol/openid-connect/token \
  -d "client_id=etudiant-service&client_secret=etudiant-secret&grant_type=client_credentials" \
  -H "Content-Type: application/x-www-form-urlencoded" | jq -r '.access_token')
```

---

## 📞 Support

Pour toute question : consultez les logs avec :
```bash
docker-compose logs -f [service-name]
```

Services clés à monitorer :
- `docker-compose logs -f etudiant-service`
- `docker-compose logs -f keycloak`
- `docker-compose logs -f api-gateway`

---

## ✅ Validation fonctionnelle rapide

### 1. Lancer l’architecture complète
```bash
docker-compose up -d
```

### 2. Vérifier les services clés
```bash
curl -I http://localhost:8761
curl -I http://localhost:8080
curl -I http://localhost:8956/actuator/health
curl -I http://localhost:8081/actuator/health
curl -I http://localhost:8082/actuator/health
curl -I http://localhost:8089/actuator/health
```

### 3. Obtenir un token Keycloak pour chaque rôle
```bash
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/realms/EduRealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=frontend" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin123" | jq -r '.access_token')

TEACHER_TOKEN=$(curl -s -X POST http://localhost:8080/realms/EduRealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=frontend" \
  -d "grant_type=password" \
  -d "username=enseignant1" \
  -d "password=password123" | jq -r '.access_token')

STUDENT_TOKEN=$(curl -s -X POST http://localhost:8080/realms/EduRealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=frontend" \
  -d "grant_type=password" \
  -d "username=etudiant1" \
  -d "password=password123" | jq -r '.access_token')
```

### 4. Tester les endpoints critiques

#### a) `GET /etudiants/mon-profil`
```bash
curl -i -H "Authorization: Bearer $STUDENT_TOKEN" http://localhost:8956/etudiants/mon-profil
curl -i -H "Authorization: Bearer $TEACHER_TOKEN" http://localhost:8956/etudiants/mon-profil
curl -i -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8956/etudiants/mon-profil
```
- Attendu : `200` pour étudiant, `403` pour enseignant, `200` pour admin si admin a droit.

#### b) `GET /etudiants/allEtudiants`
```bash
curl -i -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8956/etudiants/allEtudiants
curl -i -H "Authorization: Bearer $TEACHER_TOKEN" http://localhost:8956/etudiants/allEtudiants
curl -i -H "Authorization: Bearer $STUDENT_TOKEN" http://localhost:8956/etudiants/allEtudiants
```
- Attendu : `200` pour admin/enseignant, `403` pour étudiant.

#### c) `POST /api/examens/createExamen`
```bash
curl -i -X POST http://localhost:8956/api/examens/createExamen \
  -H "Authorization: Bearer $TEACHER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titre":"Test Note","description":"Validation","dateExamen":"2026-06-15T10:00:00"}'

curl -i -X POST http://localhost:8956/api/examens/createExamen \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titre":"Test Note","description":"Validation","dateExamen":"2026-06-15T10:00:00"}'

curl -i -X POST http://localhost:8956/api/examens/createExamen \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titre":"Test Note","description":"Validation","dateExamen":"2026-06-15T10:00:00"}'
```
- Attendu : `200` pour enseignant/admin, `403` pour étudiant.

#### d) `DELETE /api/enseignants/delete/{id}`
```bash
curl -i -X DELETE http://localhost:8956/api/enseignants/delete/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN"

curl -i -X DELETE http://localhost:8956/api/enseignants/delete/1 \
  -H "Authorization: Bearer $TEACHER_TOKEN"
```
- Attendu : `200` pour admin, `403` pour enseignant.

### 5. Tester la navigation frontend et masquage des menus

Le frontend utilise `localStorage.user.role` et le guard `RoleGuard`.
- `ADMIN` doit voir `admin/statistiques`, `enseignants`, `cours`, `examens`
- `ENSEIGNANT` doit voir `enseignant/listExamen`, `cours`, `examens`
- `ETUDIANT` doit voir `/etudiant/dashboard`, `/etudiant/cours`, `/etudiant/resultats`

**Front-end recommandé :** décoder le JWT et normaliser les rôles
```ts
const payload = JSON.parse(atob(token.split('.')[1]));
const roles = payload.realm_access?.roles || [];
const role = roles.includes('admin') ? 'ADMIN'
  : roles.includes('teacher') ? 'ENSEIGNANT'
  : roles.includes('student') ? 'ETUDIANT'
  : null;
``` 

### 6. Utiliser le script de validation automatique
Un script de vérification existe déjà dans le projet : `test-services.sh`
```bash
chmod +x test-services.sh
./test-services.sh
```

### 7. Récapitulatif des statuts attendus
| Endpoint | Admin | Enseignant | Étudiant |
|---|---|---|---|
| `/etudiants/mon-profil` | 200 | 403 | 200 |
| `/etudiants/allEtudiants` | 200 | 200 | 403 |
| `/api/examens/createExamen` | 200 | 200 | 403 |
| `/api/enseignants/delete/{id}` | 200 | 403 | 403 |

---

**Dernière mise à jour :** 06/06/2024  
**Version :** 1.0  
**Statut d'évaluation :** Prêt pour soutenance 🚀
