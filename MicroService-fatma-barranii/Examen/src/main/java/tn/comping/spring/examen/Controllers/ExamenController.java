package tn.comping.spring.examen.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.comping.spring.examen.Services.ExamenService;
import tn.comping.spring.examen.dto.ExamenRequestDTO;
import tn.comping.spring.examen.dto.ExamenResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/examens")
@RequiredArgsConstructor
public class ExamenController {
    private final ExamenService service;

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
}
