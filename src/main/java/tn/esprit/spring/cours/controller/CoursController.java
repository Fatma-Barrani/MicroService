package tn.esprit.spring.cours.controller;

import tn.esprit.spring.cours.dto.CoursDTO;
import tn.esprit.spring.cours.service.CoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cours")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CoursController {

    private final CoursService coursService;

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<CoursDTO> createCours(@Valid @RequestBody CoursDTO coursDTO) {
        return new ResponseEntity<>(coursService.createCours(coursDTO), HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping("/all")
    public ResponseEntity<List<CoursDTO>> getAllCours() {
        return ResponseEntity.ok(coursService.getAllCours());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CoursDTO> getCoursById(@PathVariable Long id) {
        return ResponseEntity.ok(coursService.getCoursById(id));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<CoursDTO> updateCours(@PathVariable Long id, @RequestBody CoursDTO coursDTO) {
        return ResponseEntity.ok(coursService.updateCours(id, coursDTO));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCours(@PathVariable Long id) {
        coursService.deleteCours(id);
        return ResponseEntity.noContent().build();
    }

    // ===== MÉTHODE MÉTIER : Recherche par catégorie =====
    @GetMapping("/search/categorie")
    public ResponseEntity<List<CoursDTO>> searchByCategorie(@RequestParam String categorie) {
        return ResponseEntity.ok(coursService.rechercherParCategorie(categorie));
    }
}