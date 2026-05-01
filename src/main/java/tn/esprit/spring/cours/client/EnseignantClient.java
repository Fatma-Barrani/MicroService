package tn.esprit.spring.cours.client;

import tn.esprit.spring.cours.dto.EnseignantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 * 🔵 COMMUNICATION SYNCHRONE avec le microservice ENSEIGNANT
 * Via OpenFeign - Appel REST bloquant
 */
@FeignClient(name = "MicroServiceProject", url = "http://localhost:8083")
public interface EnseignantClient {

    @GetMapping("/api/enseignants/getEnseignant/{id}")
    EnseignantDTO getEnseignantById(@PathVariable("id") Long id);

    @GetMapping("/api/enseignants/ListEnseignant")
    List<EnseignantDTO> getAllEnseignants();
}