package com.edunet.etudiant.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
    if (realmAccess == null || !realmAccess.containsKey("roles")) {
        return Collections.emptyList();
    }

       @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) realmAccess.get("roles");
    
    return roles.stream()
        .map(this::mapRole)
        .collect(Collectors.toList());
}


    private GrantedAuthority mapRole(String role) {
        switch (role) {
            case "student":
                return new SimpleGrantedAuthority("ROLE_ETUDIANT");
            case "teacher":
                return new SimpleGrantedAuthority("ROLE_ENSEIGNANT");
            case "admin":
                return new SimpleGrantedAuthority("ROLE_ADMIN");
            default:
                return new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
        }
    }
}
