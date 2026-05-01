package tn.comping.spring.examen.Services;

import tn.comping.spring.examen.dto.ExamenRequestDTO;
import tn.comping.spring.examen.dto.ExamenResponseDTO;

import java.util.List;

public interface ExamenService {
    ExamenResponseDTO create(ExamenRequestDTO dto);

    ExamenResponseDTO getById(Long id);

    List<ExamenResponseDTO> getAll();

    ExamenResponseDTO update(Long id, ExamenRequestDTO dto);

    void delete(Long id);
    
     void assignExamenToEnseignant(Long examenId, Long enseignantId);
}
