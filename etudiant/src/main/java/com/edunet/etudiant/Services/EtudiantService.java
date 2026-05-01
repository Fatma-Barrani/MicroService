package com.edunet.etudiant.Services;

import com.edunet.etudiant.Entities.Etudiant;

import java.util.List;
import java.util.Map;

public interface EtudiantService {
    Etudiant updateEtudiant(Long id, Etudiant etudiant);

    String deleteEtudiant(Long id);

    Etudiant addEtudiant(Etudiant etudiant);

    List<Etudiant> getAllEtudiants();

    Etudiant getEtudiantById(Long id);

    Map<String, Object> getStatistiques();

    Map<String, Object> getStatistiquesParMatiere(String matiere);
}
