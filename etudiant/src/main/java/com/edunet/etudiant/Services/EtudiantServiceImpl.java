package com.edunet.etudiant.Services;

import com.edunet.etudiant.Entities.Etudiant;
import com.edunet.etudiant.Repositories.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class EtudiantServiceImpl implements EtudiantService {
    @Autowired
    private EtudiantRepository etudiantRepository;

    public Etudiant addEtudiant(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    public Etudiant updateEtudiant(Long id, Etudiant newEtudiant) {
        if (etudiantRepository.findById(id).isPresent()) {
            Etudiant existing = etudiantRepository.findById(id).get();
            existing.setNom(newEtudiant.getNom());
            existing.setPrenom(newEtudiant.getPrenom());
            existing.setEmail(newEtudiant.getEmail());
            existing.setFiliere(newEtudiant.getFiliere());
            existing.setAnneeInscription(newEtudiant.getAnneeInscription());
            existing.setMoyenneGenerale(newEtudiant.getMoyenneGenerale());
            return etudiantRepository.save(existing);
        } else {
            return null;
        }
    }

    public Etudiant getEtudiantById(Long id) {
        return etudiantRepository.findById(id).orElse(null);
    }

    public String deleteEtudiant(Long id) {
        if (etudiantRepository.findById(id).isPresent()) {
            etudiantRepository.deleteById(id);
            return "Étudiant supprimé";
        } else {
            return "Étudiant non trouvé";
        }
    }


}