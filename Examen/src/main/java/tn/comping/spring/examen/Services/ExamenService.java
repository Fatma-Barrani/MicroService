package tn.comping.spring.examen.Services;

import tn.comping.spring.examen.Entites.Examen;
import tn.comping.spring.examen.dto.ExamenRequestDTO;
import tn.comping.spring.examen.dto.ExamenResponseDTO;

import java.util.List;

public interface ExamenService {
    ExamenResponseDTO create(ExamenRequestDTO dto);

    ExamenResponseDTO getById(Long id);

    List<ExamenResponseDTO> getAll();

    ExamenResponseDTO update(Long id, ExamenRequestDTO dto);

    void delete(Long id);
    Examen affecterEnseignant(Long examenId, Long enseignantId);
    void affecterEnseignantAsync(Long examenId, Long enseignantId);
    List<ExamenResponseDTO> filterExamen(String matiere, String niveau, String statut);
    List<ExamenResponseDTO> sortExamens(List<Examen> examens, String sortBy, String direction);
}
