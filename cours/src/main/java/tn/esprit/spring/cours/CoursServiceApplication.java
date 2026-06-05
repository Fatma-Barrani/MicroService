package tn.esprit.spring.cours;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient  // Pour Eureka
@EnableFeignClients     // Pour OpenFeign (communication SYNCHRONE)
public class CoursServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoursServiceApplication.class, args);
        System.out.println(" CoursServices démarré sur le port 8082");
        System.out.println(" Communication SYNCHRONE (OpenFeign) → Enseignant");
        System.out.println(" Communication ASYNCHRONE (RabbitMQ) → Examen");
    }
}