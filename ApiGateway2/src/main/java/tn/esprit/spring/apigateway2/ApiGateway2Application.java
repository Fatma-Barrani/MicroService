package tn.esprit.spring.apigateway2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGateway2Application {

    public static void main(String[] args) {
        SpringApplication.run(ApiGateway2Application.class, args);
    }
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route("enseignants-service", r -> r.path("/api/enseignants/**")
                        .uri("lb://MicroServiceProject"))

                .route("examens-service", r -> r.path("/api/examens/**")
                        .uri("lb://Examen"))

                .route("etudiant-service", r -> r.path("/etudiants/**")
                        .uri("lb://ETUDIANT-SERVICE"))

                .route("cours-service", r -> r.path("/api/cours/**")
                        .uri("lb://CoursServices"))


                .route("auth-route", r -> r.path("/api/auth/**")
                        .uri("lb://ETUDIANT-SERVICE"))

                // Route inscription → nest-backend direct (NestJS non enregistré)
                .route("inscription-route",
                    r -> r.path("/api/inscriptions/**")
                    .filters(f -> f.rewritePath(
                        "/api/inscriptions/(?<seg>.*)",
                        "/auth/${seg}"))
                    .uri("http://nest-backend:3000"))

                .build();
    }
}
