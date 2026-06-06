package tn.esprit.spring.microserviceproject.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.spring.microserviceproject.Dtos.EtudiantDto;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EtudiantClient {

    private final RestTemplate restTemplate;

    private final String ETUDIANT_URL = "http://api-gateway:8956/etudiants/by-filiere/";

    public List<EtudiantDto> getByFiliere(String filiere) {

        try {
            EtudiantDto[] response = restTemplate.getForObject(
                    ETUDIANT_URL + filiere,
                    EtudiantDto[].class
            );

            if (response == null) {
                return List.of();
            }

            return Arrays.asList(response);

        } catch (Exception e) {
            System.out.println("❌ Etudiant MS call failed: " + e.getMessage());
            return List.of(); // IMPORTANT: never crash main flow
        }
    }
}