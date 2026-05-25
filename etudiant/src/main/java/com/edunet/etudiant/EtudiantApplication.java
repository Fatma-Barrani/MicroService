package com.edunet.etudiant;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import com.edunet.etudiant.Repositories.EtudiantRepository;
import com.edunet.etudiant.Entities.Etudiant;

@SpringBootApplication
@EnableDiscoveryClient
public class EtudiantApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtudiantApplication.class, args);
    }

    @Bean
    ApplicationRunner init(EtudiantRepository repo) {
        return args -> {

            if (repo.count() == 0) {

                repo.save(new Etudiant("Melki", "Amal", "amal@example.com", "Mathématique", 2023, 15.5));
                repo.save(new Etudiant("Barrani", "Fatma", "fatma@example.com", "Physique", 2022, 17.0));
                repo.save(new Etudiant("Dridi", "Arwa", "arwa@example.com", "Informatique", 2023, 14.0));
                repo.save(new Etudiant("Sallemi", "Mariem", "mariem@example.com", "Philosophie", 2021, 16.0));

                repo.findAll().forEach(System.out::println);
            }
        };
    }
}