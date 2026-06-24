package com.edunet.etudiant.controller;

import com.edunet.etudiant.Dtos.CoursDTO;
import com.edunet.etudiant.Dtos.EtudiantRequestDTO;
import com.edunet.etudiant.Dtos.EtudiantResponseDTO;
import com.edunet.etudiant.Entities.Etudiant;
import com.edunet.etudiant.Services.CoursClient;
import com.edunet.etudiant.Services.EtudiantService;
import com.edunet.etudiant.Utils.EtudiantMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.edunet.etudiant.Dtos.ParticipationDTO;
import com.edunet.etudiant.Services.ExamenClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/etudiants")
public class EtudiantController {

@Autowired
private RabbitTemplate rabbitTemplate;

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private EtudiantMapper etudiantMapper;

    @Autowired
    private ExamenClient examenClient;

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

    @PostMapping("/addEtudiant")
    public ResponseEntity<EtudiantResponseDTO> addEtudiant(@RequestBody EtudiantRequestDTO requestDTO) {
        Etudiant entity = etudiantMapper.toEntity(requestDTO);
        Etudiant saved = etudiantService.addEtudiant(entity);
        EtudiantResponseDTO response = etudiantMapper.toResponseDTO(saved);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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

    // ---------- Fonctionnalité avancée : statistiques (avec appel synchrone vers Examen via Feign dans le service) ----------
    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        return new ResponseEntity<>(etudiantService.getStatistiques(), HttpStatus.OK);
    }

    @GetMapping("/statsParMatiere/{matiere}")
    public ResponseEntity<Map<String, Object>> getStatsByMatiere(@PathVariable String matiere) {
        return ResponseEntity.ok(etudiantService.getStatistiquesParMatiere(matiere));
    }
    // ========== COMMUNICATION AVEC COURS (via Feign) ==========
    @Autowired
    private CoursClient coursClient;

    @GetMapping("/cours/all")
    public ResponseEntity<List<CoursDTO>> getTousLesCours() {
        return ResponseEntity.ok(coursClient.getAllCours());
    }

    @GetMapping("/cours/search")
    public ResponseEntity<List<CoursDTO>> rechercherCoursParCategorie(@RequestParam String categorie) {
        return ResponseEntity.ok(coursClient.searchByCategorie(categorie));
    }

    @GetMapping("/{id}/participations")
public ResponseEntity<List<ParticipationDTO>> getParticipationsByEtudiant(
        @PathVariable Long id) {
    List<ParticipationDTO> participations = examenClient.getParticipationsByEtudiant(id);
    return ResponseEntity.ok(participations);
}
@GetMapping("/{id}/notif-enseignant")
public ResponseEntity<String> notifierEnseignant(
        @PathVariable Long id,
        @RequestParam String action) {
    try {
        // Créer le message
        Map<String, String> message = new HashMap<>();
        message.put("etudiantId", String.valueOf(id));
        message.put("action", action);
        message.put("timestamp", LocalDateTime.now().toString());
        
        // Envoyer via RabbitMQ
        rabbitTemplate.convertAndSend("examen_exchange", "etudiant.notification", message);
        
        return ResponseEntity.ok("✅ Notification envoyée à l'enseignant pour l'action: " + action);
    } catch (Exception e) {
        return ResponseEntity.status(500).body("❌ Erreur: " + e.getMessage());
    }
}
}



