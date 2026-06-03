package com.edunet.etudiant.Services;

import com.edunet.etudiant.Dtos.*;
import com.edunet.etudiant.Entities.Etudiant;
import com.edunet.etudiant.Repositories.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *  communications réelles :
 *  SYNC 1  : inscrireAExamen()        → Feign POST /api/examens/participer
 *  SYNC 2  : assignerExamen()         → Feign PUT  /api/enseignants/{id}/assignExamen/{id}
 *  ASYNC 1 : sendEtudiantEvent()      → etudiant.queue
 *  ASYNC 2 : sendNotifEnseignant()    → notif.enseignant.queue
 */
@Service
public class EtudiantServiceImpl implements EtudiantService {

    @Autowired private EtudiantRepository etudiantRepository;
    @Autowired private ExamenClient       examenClient;
    @Autowired private EnseignantClient   enseignantClient;
    @Autowired private EtudiantProducer   etudiantProducer;

    // ── CRUD ──────────────────────────────────────────────────────────────

    @Override
    public Etudiant addEtudiant(Etudiant etudiant) {
        Etudiant saved = etudiantRepository.save(etudiant);

        // ✅ ASYNC 1 → etudiant.queue → consommé par EtudiantConsumer chez Arwa
        try {
            EtudiantEventDTO ev = toEvent(saved, "INSCRIPTION");
            etudiantProducer.sendEtudiantEvent(ev);
            System.out.println("📤 [ASYNC 1 → Examen MS] etudiant.queue : " + saved.getNom());
        } catch (Exception e) {
            System.err.println("⚠️ ASYNC 1 indisponible : " + e.getMessage());
        }

        // ✅ ASYNC 2 → notif.enseignant.queue → consommé par EtudiantNotifConsumer
        try {
            etudiantProducer.sendNotifEnseignant(toNotif(saved, "INSCRIPTION"));
            System.out.println("📤 [ASYNC 2 → Enseignant MS] notif.enseignant.queue : " + saved.getNom());
        } catch (Exception e) {
            System.err.println("⚠️ ASYNC 2 indisponible : " + e.getMessage());
        }

        return saved;
    }

    @Override
    public Etudiant updateEtudiant(Long id, Etudiant n) {
        return etudiantRepository.findById(id).map(e -> {
            e.setNom(n.getNom()); e.setPrenom(n.getPrenom()); e.setEmail(n.getEmail());
            e.setFiliere(n.getFiliere()); e.setAnneeInscription(n.getAnneeInscription());
            e.setMoyenneGenerale(n.getMoyenneGenerale());
            Etudiant updated = etudiantRepository.save(e);

            // ASYNC 1 + ASYNC 2 aussi sur update
            try { etudiantProducer.sendEtudiantEvent(toEvent(updated, "MISE_A_JOUR")); } catch (Exception ex) { /* ignore */ }
            try { etudiantProducer.sendNotifEnseignant(toNotif(updated, "MISE_A_JOUR_NOTE")); } catch (Exception ex) { /* ignore */ }

            return updated;
        }).orElse(null);
    }

    @Override
    public List<Etudiant> getAllEtudiants() { return etudiantRepository.findAll(); }

    @Override
    public Etudiant getEtudiantById(Long id) { return etudiantRepository.findById(id).orElse(null); }

    @Override
    public String deleteEtudiant(Long id) {
        if (etudiantRepository.existsById(id)) { etudiantRepository.deleteById(id); return "Étudiant supprimé"; }
        return "Étudiant non trouvé";
    }

    @Override
    public List<Etudiant> findByFiliere(String filiere) {
        List<Etudiant> res = etudiantRepository.findByFiliere(filiere.trim().toLowerCase());
        return res != null ? res : List.of();
    }

    // ── ✅ SYNC 1 — Feign → MS Examen (Arwa) ─────────────────────────────
    // Inscrire un étudiant à un examen
    // → appelle POST /api/examens/participer chez Arwa
    // → crée une Participation en base chez Arwa
    @Override
    public ParticipationDTO inscrireAExamen(Long etudiantId, Long examenId) {
        Etudiant e = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable : " + etudiantId));
        System.out.println("🔵 Inscription " + e.getNom() + " à examen #" + examenId);
        ParticipationDTO p = examenClient.inscrireEtudiantAExamen(etudiantId, examenId);
        System.out.println(" Participation créée ID=" + p.getId());
        return p;
    }

    // ── ✅ SYNC 2 — Feign → MS Enseignant  ────────────────────────
    // Assigner un examen à un enseignant
    // → appelle PUT /api/enseignants/{ensId}/assignExamen/{examId}
    // → met à jour enseignant.examensIds
    @Override
    public EnseignantDTO assignerExamen(Long enseignantId, Long examenId) {
        System.out.println("🔵  Assigner examen #" + examenId + " → enseignant #" + enseignantId);
        EnseignantDTO result = enseignantClient.assignerExamen(enseignantId, examenId);
        System.out.println("✅ Enseignant mis à jour chez Fatma");
        return result;
    }

    // ── FONCTIONNALITE AVANCEE :STATISTIQUES ──────────────────────────────────────────────────────
    @Override
    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();
        List<Etudiant> all = etudiantRepository.findAll();
        stats.put("totalEtudiants", all.size());
        stats.put("moyenneGlobale", all.stream().mapToDouble(Etudiant::getMoyenneGenerale).average().orElse(0));
        stats.put("repartitionParFiliere", all.stream()
                .collect(Collectors.groupingBy(Etudiant::getFiliere, Collectors.counting())));
        try {
            List<ExamenDTO> ex = examenClient.getAllExamens();
            stats.put("totalExamens", ex.size());
            stats.put("coefficientMoyenExamens", ex.stream().mapToDouble(ExamenDTO::getCoefficient).average().orElse(0));
        } catch (Exception e) { stats.put("examenServiceError", "Service Examen indisponible"); }
        try {
            stats.put("totalEnseignants", enseignantClient.getAllEnseignants().size());
        } catch (Exception e) { stats.put("enseignantServiceError", "Service Enseignant indisponible"); }
        return stats;
    }
//
    @Override
    public Map<String, Object> getStatistiquesParMatiere(String matiere) {
        Map<String, Object> stats = new HashMap<>();
        try {
            List<ExamenDTO> ex = examenClient.getExamensByMatiere(matiere);
            stats.put("examens", ex); stats.put("totalExamens", ex.size());
            stats.put("coefficientMoyen", ex.stream().mapToDouble(ExamenDTO::getCoefficient).average().orElse(0));
        } catch (Exception e) { stats.put("error", "Service Examen indisponible pour : " + matiere); }
        return stats;
    }

    @Override
    public List<EnseignantDTO> getAllEnseignants() {
        return enseignantClient.getAllEnseignants();
    }

    // ── HELPERS ───────────────────────────────────────────────────────────
    private EtudiantEventDTO toEvent(Etudiant e, String action) {
        return EtudiantEventDTO.builder()
                .id(e.getId()).nom(e.getNom()).prenom(e.getPrenom()).email(e.getEmail())
                .filiere(e.getFiliere()).anneeInscription(e.getAnneeInscription())
                .moyenneGenerale(e.getMoyenneGenerale()).action(action).build();
    }
    private NotifEnseignantEvent toNotif(Etudiant e, String action) {
        return NotifEnseignantEvent.builder()
                .etudiantId(e.getId()).etudiantNom(e.getNom()).etudiantPrenom(e.getPrenom())
                .etudiantEmail(e.getEmail()).filiere(e.getFiliere())
                .moyenneGenerale(e.getMoyenneGenerale()).action(action).build();
    }
}
