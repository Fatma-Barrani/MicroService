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

                .build();
    }
}
