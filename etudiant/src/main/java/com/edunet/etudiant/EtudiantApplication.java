package com.edunet.etudiant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import com.edunet.etudiant.Repositories.EtudiantRepository;
import com.edunet.etudiant.Entities.Etudiant;

@SpringBootApplication
@EnableDiscoveryClient   // pour enregistrer le service dans Eureka
//@EnableFeignClients     // pour utiliser Feign (communication synchrone)
public class EtudiantApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtudiantApplication.class, args);
    }

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Bean
    ApplicationRunner init() {
        return (args) -> {
            // Ajout de quelques étudiants de test (avec moyenne générale)
            etudiantRepository.save(new Etudiant("Melki", "Amal", "amal@example.com", "Mathématique", 2023, 15.5));
            etudiantRepository.save(new Etudiant("Barrani", "Fatma", "fatma@example.com", "physique", 2022, 17.0));
            etudiantRepository.save(new Etudiant("Dridi", "Arwa", "arwa@example.com", "informatique", 2023, 14.0));
            etudiantRepository.save(new Etudiant("Sallemi", "Mariem", "mariem@example.com", "philosophie", 2021, 16.0));

            // Affichage dans la console
            etudiantRepository.findAll().forEach(System.out::println);
        };
    }
}