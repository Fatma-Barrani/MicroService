package tn.esprit.spring.appdistribuee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class AppDistribueeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppDistribueeApplication.class, args);
    }

}
