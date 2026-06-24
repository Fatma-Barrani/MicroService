package com.edunet.etudiant.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${keycloak.auth-server-url:http://keycloak:8080}")
    private String keycloakUrl;

    @Value("${keycloak.realm:EduRealm}")
    private String realm;

    // Login Keycloak - appelé par Angular
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> credentials) {
        try {
            RestTemplate rest = new RestTemplate();
            String url = keycloakUrl + "/realms/" + realm
                + "/protocol/openid-connect/token";

            MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", "etudiant-service");
            body.add("client_secret", "etudiant-secret");
            body.add("username", credentials.get("username"));
            body.add("password", credentials.get("password"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED);

            ResponseEntity<Map> response = rest.postForEntity(
                url, new HttpEntity<>(body, headers), Map.class);

            String token = (String) response.getBody()
                .get("access_token");
            String role = extractRole(token);

            return ResponseEntity.ok(Map.of(
                "access_token", token,
                "user", Map.of(
                    "role", role,
                    "username", credentials.get("username")
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Identifiants invalides"));
        }
    }

    // Register - délègue au nest-backend MongoDB
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Map<String, String> data) {
        try {
            RestTemplate rest = new RestTemplate();
            ResponseEntity<Map> response = rest.postForEntity(
                "http://nest-backend:3000/auth/register",
                data, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error",
                    "Erreur inscription: " + e.getMessage()));
        }
    }

    // Extraire rôle depuis JWT Keycloak
    private String extractRole(String token) {
        try {
            String payload = token.split("\\.")[1];
            String decoded = new String(
                java.util.Base64.getUrlDecoder().decode(payload));
            if (decoded.contains("\"student\"")) return "ETUDIANT";
            if (decoded.contains("\"teacher\"")) return "ENSEIGNANT";
            if (decoded.contains("\"admin\""))   return "ADMIN";
        } catch (Exception ignored) {}
        return "ETUDIANT";
    }
}
