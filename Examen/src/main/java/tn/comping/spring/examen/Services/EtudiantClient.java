package tn.comping.spring.examen.Services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.comping.spring.examen.dto.EtudiantDTO;

@FeignClient(name = "ETUDIANT-SERVICE")
public interface EtudiantClient {
    @GetMapping("/etudiants/{id}")
    EtudiantDTO getEtudiantById(@PathVariable Long id);
}
