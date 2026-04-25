package tn.esprit.spring.microserviceproject.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.microserviceproject.Dtos.enseignantRequestDto;
import tn.esprit.spring.microserviceproject.Dtos.enseignantResponseDto;
import tn.esprit.spring.microserviceproject.Repositories.enseignantRepository;
import tn.esprit.spring.microserviceproject.Utils.enseignantMapper;
import tn.esprit.spring.microserviceproject.entities.enseignant;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class enseignantServiceImpl implements enseignantService{
    private final enseignantRepository repository;
    private final enseignantMapper mapper;

    @Override
    public enseignantResponseDto create(enseignantRequestDto dto) {
        enseignant e = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(e));
    }

    @Override
    public List<enseignantResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public enseignantResponseDto getById(Long id) {
        enseignant e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignant not found"));
        return mapper.toDTO(e);
    }

    @Override
    public enseignantResponseDto update(Long id, enseignantRequestDto dto) {
        enseignant e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignant not found"));

        mapper.updateEntityFromDto(dto, e);
        return mapper.toDTO(repository.save(e));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
