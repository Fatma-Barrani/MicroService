package tn.comping.spring.examen.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.comping.spring.examen.Entites.Examen;
import tn.comping.spring.examen.Repositories.ExamenRepository;
import tn.comping.spring.examen.Utils.ExamenMapper;
import tn.comping.spring.examen.dto.ExamenRequestDTO;
import tn.comping.spring.examen.dto.ExamenResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamenServiceImpl implements ExamenService{
    private final ExamenRepository examenRepository;
    @Override
    public ExamenResponseDTO create(ExamenRequestDTO dto) {
        Examen examen = ExamenMapper.toEntity(dto);
        return ExamenMapper.toDto(examenRepository.save(examen));
    }

    @Override
    public ExamenResponseDTO getById(Long id) {
        Examen examen = examenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen introuvable"));

        return ExamenMapper.toDto(examen);
    }

    @Override
    public List<ExamenResponseDTO> getAll() {
        return examenRepository.findAll()
                .stream()
                .map(ExamenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ExamenResponseDTO update(Long id, ExamenRequestDTO dto) {
        Examen examen = examenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen introuvable"));

        examen.setTitre(dto.getTitre());
        examen.setDescription(dto.getDescription());
        examen.setDateExamen(dto.getDateExamen());
        examen.setDuree(dto.getDuree());
        examen.setCoefficient(dto.getCoefficient());
        examen.setNiveau(dto.getNiveau());
        examen.setMatiere(dto.getMatiere());
        examen.setStatut(dto.getStatut());

        return ExamenMapper.toDto(examenRepository.save(examen));
    }

    @Override
    public void delete(Long id) {
        examenRepository.deleteById(id);
    }
}
