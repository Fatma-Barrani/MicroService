package com.edunet.etudiant.Controllers;

import com.edunet.etudiant.Dtos.EtudiantRequestDTO;
import com.edunet.etudiant.Dtos.EtudiantResponseDTO;
import com.edunet.etudiant.Entities.Etudiant;
import com.edunet.etudiant.Services.EtudiantService;
import com.edunet.etudiant.Utils.EtudiantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/etudiants")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private EtudiantMapper etudiantMapper;

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


