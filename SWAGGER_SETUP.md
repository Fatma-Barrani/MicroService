# Swagger/OpenAPI Configuration pour API Gateway

## Installation

1. **Ajouter la dépendance dans `ApiGateway2/pom.xml` :**

```xml
<!-- Après spring-cloud-starter-gateway -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.4</version>
</dependency>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.0.4</version>
</dependency>
```

2. **Créer `ApiGateway2/src/main/resources/application.yml` :**

```yaml
springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    url: /v3/api-docs
    urls:
      - name: "Etudiant Service"
        url: "/etudiants/v3/api-docs"
      - name: "Cours Service"
        url: "/cours/v3/api-docs"
      - name: "Examen Service"
        url: "/examen/v3/api-docs"
      - name: "Enseignant Service"
        url: "/enseignants/v3/api-docs"
    enable-deepen-schema: true
    show-extensions: true
    operations-sorter: "method"
    tags-sorter: "alpha"
  api-docs:
    path: /v3/api-docs
    enabled: true
```

3. **Accès :**
- http://localhost:8956/swagger-ui.html
- http://localhost:8956/v3/api-docs

## Configuration pour chaque microservice

Ajouter dans chaque `application.properties` :

```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.show-extensions=true
```

## Annotations OpenAPI (optionnelles mais recommandées)

```java
@RestController
@RequestMapping("/etudiants")
@Tag(name = "Etudiant Management", description = "APIs for managing students")
public class EtudiantController {
    
    @GetMapping
    @Operation(summary = "Get all students", description = "Returns a list of all students")
    public List<Etudiant> getAll() { ... }
    
    @PostMapping
    @Operation(summary = "Create student")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Student created successfully"
    )
    public Etudiant create(@RequestBody EtudiantDTO dto) { ... }
}
```

## Déploiement

Après modifications, rebuild :
```bash
docker-compose build api-gateway
docker-compose restart api-gateway
```
