package tn.comping.spring.examen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ExamenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamenApplication.class, args);
    }

}
