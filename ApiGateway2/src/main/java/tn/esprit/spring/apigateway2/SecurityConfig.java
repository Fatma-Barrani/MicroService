package tn.esprit.spring.apigateway2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(Customizer.withDefaults())
            .authorizeExchange(exchange -> exchange
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers(
                    "/actuator/**",
                    "/eureka/**",
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/inscriptions/register",
                    "/api/inscriptions/login"
                ).permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
            .build();
    }

   @Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setMaxAge(3600L);
    config.setAllowCredentials(true);
    config.setExposedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Total-Count"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsWebFilter(source);
}

@Bean
public WebFilter corsHeaderRemovalFilter() {
    return (exchange, chain) ->
        chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            java.util.List<String> origins = headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);

            if (origins != null) {
                boolean hasWildcard = origins.stream().anyMatch("*"::equals);
                if (origins.size() > 1 || hasWildcard) {
                    headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                    headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200");
                }
            }
        }));
}

@Bean
public GlobalFilter corsCleanupGlobalFilter() {
    return (exchange, chain) -> {
        // Register a beforeCommit callback to enforce a single specific origin
        exchange.getResponse().beforeCommit(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            java.util.List<String> origins = headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
            if (origins != null) {
                boolean hasWildcard = origins.stream().anyMatch("*"::equals);
                if (origins.size() > 1 || hasWildcard) {
                    headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                    headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200");
                }
            }
            return Mono.empty();
        });
        return chain.filter(exchange);
    };
}
}