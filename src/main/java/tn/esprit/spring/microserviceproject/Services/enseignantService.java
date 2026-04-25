package tn.esprit.spring.microserviceproject.Services;

import tn.esprit.spring.microserviceproject.Dtos.enseignantRequestDto;
import tn.esprit.spring.microserviceproject.Dtos.enseignantResponseDto;

import java.util.List;

public interface enseignantService {
    enseignantResponseDto create(enseignantRequestDto dto);

    List<enseignantResponseDto> getAll();

    enseignantResponseDto getById(Long id);

    enseignantResponseDto update(Long id, enseignantRequestDto dto);

    void delete(Long id);
}
