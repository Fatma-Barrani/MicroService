package com.edunet.etudiant.Services;

import com.edunet.etudiant.Dtos.ExamenDTO;
import com.edunet.etudiant.Dtos.ParticipationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "Examen")
public interface ExamenClient {
    @GetMapping("/api/examens/GetAllExamens")
    List<ExamenDTO> getAllExamens();

    @GetMapping("/api/examens/filter")
    List<ExamenDTO> getExamensByMatiere(@RequestParam("matiere") String matiere);

    @GetMapping("/api/examens/participations/etudiant/{etudiantId}")
    List<ParticipationDTO> getParticipationsByEtudiant(@PathVariable("etudiantId") Long etudiantId);
}