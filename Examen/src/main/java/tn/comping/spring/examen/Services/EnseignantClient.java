package tn.comping.spring.examen.Services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.comping.spring.examen.dto.EnseignantDTO;
@FeignClient(name = "MicroServiceProject")
public interface EnseignantClient {
    @GetMapping("/api/enseignants/getEnseignant/{id}")
    EnseignantDTO getEnseignantById(@PathVariable Long id);
}
