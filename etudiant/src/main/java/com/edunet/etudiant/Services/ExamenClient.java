package com.edunet.etudiant.Services;
import com.edunet.etudiant.Dtos.ExamenDTO;
import com.edunet.etudiant.Dtos.ParticipationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "Examen")   // nom exact dans application.properties d'Arwa
public interface ExamenClient {

    // Lire tous les examens (utilisé pour stats)
    @GetMapping("/api/examens/GetAllExamens")
    List<ExamenDTO> getAllExamens();

    // Lire un examen par ID
    @GetMapping("/api/examens/ExamenById/{id}")
    ExamenDTO getExamenById(@PathVariable Long id);

    // Lire les examens filtrés par matière
    @GetMapping("/api/examens/filter")
    List<ExamenDTO> getExamensByMatiere(@RequestParam("matiere") String matiere);

    /**
     *  SYNC 1 — Action réelle : inscrire un étudiant à un examen
     * Appelle POST /api/examens/participer chez Examen
     * → crée une ligne Participation dans la BDD Examen
     */
    @PostMapping("/api/examens/participer")
    ParticipationDTO inscrireEtudiantAExamen(@RequestParam("etudiantId") Long etudiantId,

                                             @RequestParam("examenId") Long examenId);


    /**
     * Récupérer les participations (notes) d'un étudiant
     * Appelle GET /api/examens/participations/etudiant/{etudiantId}
     * → utilisé dans "Mes résultats"
     */
    @GetMapping("/api/examens/participations/etudiant/{etudiantId}")
    List<ParticipationDTO> getParticipationsByEtudiant(@PathVariable("etudiantId") Long etudiantId);
}
