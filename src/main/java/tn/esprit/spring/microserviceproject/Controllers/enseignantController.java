package tn.esprit.spring.microserviceproject.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import tn.esprit.spring.microserviceproject.Dtos.enseignantRequestDto;
import tn.esprit.spring.microserviceproject.Services.ExamenClient;
import tn.esprit.spring.microserviceproject.Services.enseignantService;

@RestController
@RequestMapping("/api/enseignants")
@RequiredArgsConstructor
public class enseignantController {

    private final enseignantService service;
    private final ExamenClient examenClient;

    @Value("${welcome.message}")
    private String message;

    // ADMIN ONLY
    @PostMapping("/addEnseignant")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createEnseignant(
            @RequestBody enseignantRequestDto dto) {

        return new ResponseEntity<>(
                service.create(dto),
                HttpStatus.CREATED
        );
    }

    // USER OR ADMIN
    @GetMapping("/ListEnseignant")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/getEnseignant/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ADMIN ONLY
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateEnseignant(
            @PathVariable Long id,
            @RequestBody enseignantRequestDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    // ADMIN ONLY
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteEnseignant(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.ok("Enseignant deleted successfully");
    }

    // USER OR ADMIN
    @GetMapping("/ListExamen")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<?> getAllExamens() {
        return ResponseEntity.ok(examenClient.getAllExamens());
    }

    @GetMapping("/Examen/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<?> getExamenById(@PathVariable Long id) {
        return ResponseEntity.ok(examenClient.getExamenById(id));
    }

    // ADMIN ONLY
    @PutMapping("/{enseignantId}/assignExamen/{examenId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> assignExamenToEnseignant(
            @PathVariable Long enseignantId,
            @PathVariable Long examenId) {

        return ResponseEntity.ok(
                service.assignExamen(enseignantId, examenId)
        );
    }

    // ADMIN ONLY
    @PutMapping("/{enseignantId}/assignExamenAsync/{examenId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> assignExamenAsync(
            @PathVariable Long enseignantId,
            @PathVariable Long examenId) {

        service.assignExamenAsync(enseignantId, examenId);
        return ResponseEntity.ok("Message envoyé via RabbitMQ");
    }

    // USER OR ADMIN
    @GetMapping("/filtreEnseignant/by-examen/{examenId}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<?> getEnseignantByExamen(
            @PathVariable Long examenId) {

        return ResponseEntity.ok(
                service.getEnseignantByExamen(examenId)
        );
    }

    @GetMapping("/welcome")
    public String welcome() {
        return message;
    }
}