package tn.esprit.spring.microserviceproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroServiceProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceProjectApplication.class, args);
    }

}
