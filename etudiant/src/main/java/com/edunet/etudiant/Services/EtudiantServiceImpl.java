package com.edunet.etudiant.Services;

import com.edunet.etudiant.Dtos.EtudiantEventDTO;
import com.edunet.etudiant.Dtos.ExamenDTO;
import com.edunet.etudiant.Entities.Etudiant;
import com.edunet.etudiant.Repositories.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class EtudiantServiceImpl implements EtudiantService {

    @Autowired
    private EtudiantRepository etudiantRepository;
    // Injection du client Feign pour appeler le microservice Examen
    @Autowired
    private ExamenClient examenClient;

    @Autowired
    private EtudiantProducer etudiantProducer; //RabbitProducer


    // ==================== CRUD ====================
    @Override
    public Etudiant addEtudiant(Etudiant etudiant) {
        Etudiant saved = etudiantRepository.save(etudiant);
        try {
        // SCÉNARIO communication ASYNCHRONE : publier l'événement dans RabbitMQ
        etudiantProducer.sendEtudiantEvent(toEventDTO(saved));
        } catch (Exception e) {
            System.err.println("Erreur asynchrone (RabbitMQ) : " + e.getMessage());
        }
        return saved;
    }


    @Override
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    @Override
    public Etudiant getEtudiantById(Long id) {
        return etudiantRepository.findById(id).orElse(null);
    }

    @Override
    public Etudiant updateEtudiant(Long id, Etudiant newEtudiant) {
        return etudiantRepository.findById(id).map(existing -> {
            existing.setNom(newEtudiant.getNom());
            existing.setPrenom(newEtudiant.getPrenom());
            existing.setEmail(newEtudiant.getEmail());
            existing.setFiliere(newEtudiant.getFiliere());
            existing.setAnneeInscription(newEtudiant.getAnneeInscription());
            existing.setMoyenneGenerale(newEtudiant.getMoyenneGenerale());
            Etudiant updated = etudiantRepository.save(existing);

            // SCÉNARIO Asynchrone : publier l'événement de mise à jour
            etudiantProducer.sendEtudiantEvent(toEventDTO(updated));
            return updated;
        }).orElse(null);
    }

    private EtudiantEventDTO toEventDTO(Etudiant e) {
        EtudiantEventDTO dto = new EtudiantEventDTO();
        dto.setId(e.getId());
        dto.setNom(e.getNom());
        dto.setPrenom(e.getPrenom());
        dto.setEmail(e.getEmail());
        dto.setFiliere(e.getFiliere());
        dto.setAnneeInscription(e.getAnneeInscription());
        dto.setMoyenneGenerale(e.getMoyenneGenerale());
        return dto;
    }
    @Override
    public String deleteEtudiant(Long id) {
        if (etudiantRepository.findById(id).isPresent()) {
            etudiantRepository.deleteById(id);
            return "Étudiant supprimé";
        } else {
            return "Étudiant non trouvé";
        }
    }

    // ==================== STATISTIQUES AVEC APPEL SYNCHRONE (Feign) ====================

    @Override
    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();
        List<Etudiant> etudiants = etudiantRepository.findAll();

        // Statistiques sur les étudiants
        double moyenneGlobale = etudiants.stream()
                .mapToDouble(Etudiant::getMoyenneGenerale)
                .average().orElse(0.0);
        stats.put("moyenneGlobale", moyenneGlobale);
        stats.put("totalEtudiants", etudiants.size());

        Map<String, Long> parFiliere = etudiants.stream()
                .collect(Collectors.groupingBy(Etudiant::getFiliere, Collectors.counting()));
        stats.put("repartitionParFiliere", parFiliere);

        // Appel synchrone vers le microservice Examen (OpenFeign)
        try {
            List<ExamenDTO> examens = examenClient.getAllExamens();
            stats.put("totalExamens", examens.size());
            double coeffMoyen = examens.stream()
                    .mapToDouble(ExamenDTO::getCoefficient)
                    .average().orElse(0.0);
            stats.put("coefficientMoyenExamens", coeffMoyen);
        } catch (Exception e) {
            stats.put("examenServiceError", "Service des examens indisponible");
        }

        return stats;
    }

    // ==================== STATISTIQUES PAR MATIÈRE ====================
    @Override
    public Map<String, Object> getStatistiquesParMatiere(String matiere) {
        Map<String, Object> stats = new HashMap<>();
        try {
            // Appel Feign vers l'endpoint /filter du microservice Examen
            List<ExamenDTO> examens = examenClient.getExamensByMatiere(matiere);
            stats.put("examens", examens);
            stats.put("totalExamens", examens.size());
            double coeffMoyen = examens.stream()
                    .mapToDouble(ExamenDTO::getCoefficient)
                    .average().orElse(0.0);
            stats.put("coefficientMoyenExamens", coeffMoyen);
        } catch (Exception e) {
            stats.put("error", "Service des examens indisponible pour la matière " + matiere);
        }
        return stats;
    }
}