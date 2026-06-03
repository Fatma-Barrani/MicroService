package com.edunet.etudiant.Services;

import com.edunet.etudiant.Dtos.EnseignantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**

 *
 * name = "MicroServiceProject" ← nom EXACT dans application.properties de Fatma
 */
@FeignClient(name = "MicroServiceProject")
public interface EnseignantClient {

    @GetMapping("/api/enseignants/ListEnseignant")
    List<EnseignantDTO> getAllEnseignants();

    @GetMapping("/api/enseignants/getEnseignant/{id}")
    EnseignantDTO getEnseignantById(@PathVariable Long id);

    /**
     *  SYNC 2 — Action réelle : assigner un examen à un enseignant
     */
    @PutMapping("/api/enseignants/{enseignantId}/assignExamen/{examenId}")
    EnseignantDTO assignerExamen(
            @PathVariable Long enseignantId,
            @PathVariable Long examenId
    );
}
