package com.edunet.etudiant.Services;

import com.edunet.etudiant.Dtos.EnseignantDTO;
import com.edunet.etudiant.Dtos.ParticipationDTO;
import com.edunet.etudiant.Entities.Etudiant;

import java.util.List;
import java.util.Map;

public interface EtudiantService {
    Etudiant updateEtudiant(Long id, Etudiant etudiant);

    String deleteEtudiant(Long id);
    Etudiant addEtudiant(Etudiant etudiant);
    List<Etudiant> getAllEtudiants();
    Etudiant getEtudiantById(Long id);
    List<Etudiant> findByFiliere(String filiere);
    //  SYNC 1 : Feign → Examen   (POST /api/examens/participer)
    ParticipationDTO inscrireAExamen(Long etudiantId, Long examenId);

    //  SYNC 2 : Feign → Enseignant   (PUT /api/enseignants/{id}/assignExamen/{id})
    EnseignantDTO assignerExamen(Long enseignantId, Long examenId);

    // Stats (utilisent Feign en interne)
    Map<String, Object> getStatistiques();
    Map<String, Object> getStatistiquesParMatiere(String matiere);

    // Proxy Feign pour le frontend
    List<EnseignantDTO> getAllEnseignants();
}

