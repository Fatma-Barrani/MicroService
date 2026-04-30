package tn.esprit.spring.microserviceproject.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import tn.esprit.spring.microserviceproject.Dtos.ExamenDto;
import tn.esprit.spring.microserviceproject.Dtos.enseignantRequestDto;
import tn.esprit.spring.microserviceproject.Dtos.enseignantResponseDto;
import tn.esprit.spring.microserviceproject.Repositories.enseignantRepository;
import tn.esprit.spring.microserviceproject.Utils.enseignantMapper;
import tn.esprit.spring.microserviceproject.entities.enseignant;
import tn.esprit.spring.microserviceproject.events.AssignExamenEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class enseignantServiceImpl implements enseignantService {

    private final enseignantRepository repository;
    private final enseignantMapper mapper;
    private final ExamenClient examenClient;
    private final ExamenProducer examenProducer; // ✅ AJOUT RABBITMQ PRODUCER

    // CREATE
    @Override
    public enseignantResponseDto create(enseignantRequestDto dto) {
        enseignant e = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(e));
    }

    // GET ALL
    @Override
    public List<enseignantResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public enseignantResponseDto getById(Long id) {
        enseignant e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignant not found"));
        return mapper.toDTO(e);
    }

    // UPDATE
    @Override
    public enseignantResponseDto update(Long id, enseignantRequestDto dto) {
        enseignant e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignant not found"));

        mapper.updateEntityFromDto(dto, e);
        return mapper.toDTO(repository.save(e));
    }

    // DELETE
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ============================================
    // 🔵 SYNCHRONE (Feign - EXISTANT)
    // ============================================
    public enseignantResponseDto assignExamen(Long enseignantId, Long examenId) {

        // 1. check examen via Feign
        ExamenDto examen = examenClient.getExamenById(examenId);

        if (examen == null) {
            throw new RuntimeException("Examen not found");
        }

        // 2. get enseignant
        enseignant ens = repository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant not found"));

        // 3. init list if null
        if (ens.getExamensIds() == null) {
            ens.setExamensIds(new ArrayList<>());
        }

        // 4. add examen
        ens.getExamensIds().add(examenId);

        // 5. save
        repository.save(ens);

        return mapper.toDTO(ens);
    }

    // ============================================
    // 🟢 ASYNCHRONE (RabbitMQ - NEW)
    // ============================================
    public void assignExamenAsync(Long enseignantId, Long examenId) {

        AssignExamenEvent event =
                new AssignExamenEvent(enseignantId, examenId);

        examenProducer.sendAssignExamen(event);
    }
}