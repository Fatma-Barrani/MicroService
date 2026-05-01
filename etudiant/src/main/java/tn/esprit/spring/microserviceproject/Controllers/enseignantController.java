package tn.esprit.spring.microserviceproject.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.microserviceproject.Dtos.enseignantRequestDto;
import tn.esprit.spring.microserviceproject.Dtos.enseignantResponseDto;
import tn.esprit.spring.microserviceproject.Services.ExamenClient;
import tn.esprit.spring.microserviceproject.Services.enseignantService;
import tn.esprit.spring.microserviceproject.Dtos.ExamenDto;

import java.util.List;

@RestController
@RequestMapping("/api/enseignants")
@RequiredArgsConstructor
@CrossOrigin("*")
public class enseignantController {
    private final enseignantService service;

    private final ExamenClient examenClient;

    // CREATE
    @PostMapping("/addEnseignant")
    public enseignantResponseDto create(@RequestBody enseignantRequestDto dto) {
        return service.create(dto);
    }

    // READ ALL
    @GetMapping("/ListEnseignant")
    public List<enseignantResponseDto> getAll() {
        return service.getAll();
    }

    // READ BY ID
    @GetMapping("/getEnseignant/{id}")
    public enseignantResponseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public enseignantResponseDto update(@PathVariable Long id,
            @RequestBody enseignantRequestDto dto) {
        return service.update(id, dto);
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // GET all examens from Examen microservice
    @GetMapping("/ListExamen")
    public List<ExamenDto> getAllExamens() {
        return examenClient.getAllExamens();
    }

    // GET examen by id from Examen microservice
    @GetMapping("/Examen/{id}")
    public ExamenDto getExamenById(@PathVariable Long id) {
        return examenClient.getExamenById(id);
    }


    // =====================================
    // 🔵 SYNCHRONE (Feign)
    // =====================================
    @PutMapping("/{enseignantId}/assignExamen/{examenId}")
    public enseignantResponseDto assignExamenToEnseignant(
            @PathVariable Long enseignantId,
            @PathVariable Long examenId) {

        return service.assignExamen(enseignantId, examenId);
    }

     // =====================================
    // 🟢 ASYNCHRONE (RabbitMQ)
    // =====================================
    @PutMapping("/{enseignantId}/assignExamenAsync/{examenId}")
    public String assignExamenAsync(
            @PathVariable Long enseignantId,
            @PathVariable Long examenId) {

        service.assignExamenAsync(enseignantId, examenId);

        return "Message envoyé via RabbitMQ";
    }
}

