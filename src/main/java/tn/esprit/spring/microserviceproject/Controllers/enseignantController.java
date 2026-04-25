package tn.esprit.spring.microserviceproject.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.microserviceproject.Dtos.enseignantRequestDto;
import tn.esprit.spring.microserviceproject.Dtos.enseignantResponseDto;
import tn.esprit.spring.microserviceproject.Services.enseignantService;

import java.util.List;
@RestController
@RequestMapping("/api/enseignants")
@RequiredArgsConstructor
@CrossOrigin("*")
public class enseignantController {
    private final enseignantService service;

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
}
