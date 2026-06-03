package com.edunet.etudiant.Controllers;

import com.edunet.etudiant.Dtos.EnseignantDTO;
import com.edunet.etudiant.Dtos.EtudiantRequestDTO;
import com.edunet.etudiant.Dtos.EtudiantResponseDTO;
import com.edunet.etudiant.Entities.Etudiant;
import com.edunet.etudiant.Services.EtudiantProducer;
import com.edunet.etudiant.Services.EtudiantService;
import com.edunet.etudiant.Utils.EtudiantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.edunet.etudiant.Dtos.ParticipationDTO;
import com.edunet.etudiant.Dtos.NotifEnseignantEvent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/etudiants")
@CrossOrigin(origins = "*")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private EtudiantMapper etudiantMapper;
    @Autowired
    private EtudiantProducer etudiantProducer;

    // Endpoint de test
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, i'm the Etudiant CINFO2";
    }

    // ---------- CRUD ----------
    @GetMapping("/allEtudiants")
    public ResponseEntity<List<EtudiantResponseDTO>> getAllEtudiants() {
        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        List<EtudiantResponseDTO> responseDTOs = etudiants.stream()
                .map(etudiantMapper::toResponseDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }
    @GetMapping("/getEtudiantById/{id}")
    public ResponseEntity<EtudiantResponseDTO> getEtudiantById(@PathVariable Long id) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        if (etudiant != null) {
            EtudiantResponseDTO response = etudiantMapper.toResponseDTO(etudiant);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    /**
     * POST /etudiants/addEtudiant
     *  ASYNC 1 : publie automatiquement sur etudiant.queue → consommé par Arwa
     *  ASYNC 2 : publie automatiquement sur notif.enseignant.queue → consommé par Fatma
     */
    @PostMapping("/addEtudiant")
    public ResponseEntity<EtudiantResponseDTO> addEtudiant(@RequestBody EtudiantRequestDTO requestDTO) {
        Etudiant entity = etudiantMapper.toEntity(requestDTO);
        Etudiant saved = etudiantService.addEtudiant(entity);
        EtudiantResponseDTO response = etudiantMapper.toResponseDTO(saved);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * PUT /etudiants/updateEtudiant/{id}
     * ✅ ASYNC 1 + ASYNC 2 aussi déclenchés automatiquement
     */
    @PutMapping("/updateEtudiant/{id}")
    public ResponseEntity<EtudiantResponseDTO> updateEtudiant(@PathVariable Long id,
                                                              @RequestBody EtudiantRequestDTO requestDTO) {
        Etudiant existing = etudiantService.getEtudiantById(id);
        if (existing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        etudiantMapper.updateEntityFromDto(requestDTO, existing);
        Etudiant updated = etudiantService.updateEtudiant(id, existing);
        EtudiantResponseDTO response = etudiantMapper.toResponseDTO(updated);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/deleteEtudiant/{id}")
    public ResponseEntity<String> deleteEtudiant(@PathVariable Long id) {
        String result = etudiantService.deleteEtudiant(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/by-filiere/{filiere}")
    public ResponseEntity<List<Etudiant>> getByFiliere(@PathVariable String filiere) {
        return new ResponseEntity<>(
                etudiantService.findByFiliere(filiere),
                HttpStatus.OK);
    }
// ──────────── ✅ SYNC 1 — Feign → MS Examen () ──────────────────
    /**
     * Inscrire un étudiant à un examen
     * → Appel Feign : POST /api/examens/participer chez Arwa
     * → Crée une ligne Participation dans la BDD d'Arwa
     */
    @PostMapping("/{etudiantId}/inscrire/{examenId}")
    public ResponseEntity<?> inscrireAExamen(@PathVariable Long etudiantId,
                                             @PathVariable Long examenId) {
        try {
            ParticipationDTO p = etudiantService.inscrireAExamen(etudiantId, examenId);
            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("❌ [SYNC 1] MS Examen indisponible : " + e.getMessage());
        }
    }

    // ──────────── ✅ SYNC 2 — Feign → MS Enseignant (Fatma) ─────────────
    /**
     * Assigner un examen à un enseignant
     * → Appel Feign : PUT /api/enseignants/{ensId}/assignExamen/{examId} chez Fatma
     * → Ajoute l'examen dans enseignant.examensIds en base chez Fatma
     */
    @PutMapping("/assigner-examen/{examenId}/enseignant/{enseignantId}")
    public ResponseEntity<?> assignerExamen(@PathVariable Long examenId,
                                            @PathVariable Long enseignantId) {
        try {
            EnseignantDTO result = etudiantService.assignerExamen(enseignantId, examenId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("❌ [SYNC 2] MS Enseignant indisponible : " + e.getMessage());
        }
    }
// ──────────── ✅ ASYNC 2 — RabbitMQ → MS Enseignant (Fatma) — test manuel
    /**
     * Déclenche manuellement l'envoi d'une notification vers Enseignant MS
     * (déjà automatique dans addEtudiant/updateEtudiant, mais cet endpoint
     *  permet de tester indépendamment pour la validation prof)
     */
    @PostMapping("/{id}/notif-enseignant")
    public ResponseEntity<String> notifEnseignant(@PathVariable Long id,
                                                  @RequestParam(defaultValue = "INSCRIPTION") String action) {
        try {
            Etudiant e = etudiantService.getEtudiantById(id);
            if (e == null) return ResponseEntity.notFound().build();
            NotifEnseignantEvent event = NotifEnseignantEvent.builder()
                    .etudiantId(e.getId()).etudiantNom(e.getNom()).etudiantPrenom(e.getPrenom())
                    .etudiantEmail(e.getEmail()).filiere(e.getFiliere())
                    .moyenneGenerale(e.getMoyenneGenerale()).action(action).build();
            etudiantProducer.sendNotifEnseignant(event);
            return ResponseEntity.accepted().body(
                    "📤 [ASYNC 2] Message publié sur notif.enseignant.queue\n" +
                            "→ MS Enseignant (Fatma) consomme via EtudiantNotifConsumer\n" +
                            "→ Étudiant : " + e.getNom() + " " + e.getPrenom() +
                            " | Filière : " + e.getFiliere() + " | Action : " + action);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("❌ [ASYNC 2] RabbitMQ : " + ex.getMessage());
        }
    }
    // ──────────── Proxy Feign : liste enseignants pour le front ──────────
    @GetMapping("/enseignants-feign")
    public ResponseEntity<?> getEnseignants() {
        try {
            return ResponseEntity.ok(etudiantService.getAllEnseignants());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("❌  Enseignant indisponible : " + e.getMessage());
        }
    }

    // ---------- Fonctionnalité avancée : statistiques (avec appel synchrone vers Examen via Feign dans le service) ----------
    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        return new ResponseEntity<>(etudiantService.getStatistiques(), HttpStatus.OK);
    }

    @GetMapping("/statsParMatiere/{matiere}")
    public ResponseEntity<Map<String, Object>> getStatsByMatiere(@PathVariable String matiere) {
        return ResponseEntity.ok(etudiantService.getStatistiquesParMatiere(matiere));
    }
}



