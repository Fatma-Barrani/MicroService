package tn.comping.spring.examen.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.comping.spring.examen.Entites.Examen;
import tn.comping.spring.examen.Repositories.ExamenRepository;
import tn.comping.spring.examen.Services.ExamenService;
import tn.comping.spring.examen.dto.ExamenRequestDTO;
import tn.comping.spring.examen.dto.ExamenResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/examens")
@RequiredArgsConstructor
public class ExamenController {
    private final ExamenService service;
    private final ExamenRepository examenRepository;

    @PostMapping("/createExamen")
    public ExamenResponseDTO create(@RequestBody ExamenRequestDTO dto) {
        return service.create(dto);
    }
    @GetMapping("/ExamenById/{id}")
    public ExamenResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/GetAllExamens")
    public List<ExamenResponseDTO> getAll() {
        return service.getAll();
    }

    @PutMapping("/UpdateExamenBYId/{id}")
    public ExamenResponseDTO update(@PathVariable Long id,
                                    @RequestBody ExamenRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/DeleteExamen/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    @PutMapping("/{idExamen}/affecter/{idEns}")
    public Examen affecter(
            @PathVariable Long idExamen,
            @PathVariable Long idEns) {

        return service.affecterEnseignant(idExamen, idEns);
    }
    @PostMapping("/affecter-async")
    public ResponseEntity<String> affecterAsync(@RequestParam Long examId,
                                                @RequestParam Long teacherId) {

        service.affecterEnseignantAsync(examId, teacherId);

        return ResponseEntity.accepted()
                .body("Affectation en cours (asynchrone)");
    }
    @GetMapping("/filter")
    public List<ExamenResponseDTO> filter(
            @RequestParam(required = false) String matiere,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) String statut
    ) {
        return  service.filterExamen(matiere, niveau, statut);
    }
    @GetMapping("/sort")
    public List<ExamenResponseDTO> sort(
            @RequestParam String sortBy,
            @RequestParam String direction
    ) {
        List<Examen> examens = examenRepository.findAll();
        return service.sortExamens(examens, sortBy, direction);
    }
}
