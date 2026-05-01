package tn.comping.spring.examen.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.comping.spring.examen.Entites.Examen;
import tn.comping.spring.examen.Repositories.ExamenRepository;
import tn.comping.spring.examen.Utils.ExamenMapper;
import tn.comping.spring.examen.dto.EnseignantDTO;
import tn.comping.spring.examen.dto.ExamEvent;
import tn.comping.spring.examen.dto.ExamenRequestDTO;
import tn.comping.spring.examen.dto.ExamenResponseDTO;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamenServiceImpl implements ExamenService{
    private final ExamenRepository examenRepository;
    private final EnseignantClient enseignantClient;
    private final ExamProducer examProducer;
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

    @Override
    public Examen affecterEnseignant(Long examenId, Long enseignantId) {
        EnseignantDTO ens = enseignantClient.getEnseignantById(enseignantId);

        if (ens == null) {
            throw new RuntimeException("Enseignant introuvable");
        }

        Examen examen = examenRepository.findById(examenId)
                .orElseThrow();

        examen.setEnseignantId(enseignantId);
        System.out.println("🔥 Affectation SYNCHRONE");
        Examen saved = examenRepository.save(examen);
        System.out.println("🔥 ENTER affecterEnseignant");

        return saved;
    }

    @Override
    public void affecterEnseignantAsync(Long examenId, Long enseignantId) {
        ExamEvent event = new ExamEvent(examenId, enseignantId);

        examProducer.sendTeacherAssignedEvent(examenId, enseignantId);

        System.out.println("📤 Affectation ASYNCHRONE envoyée");
    }

    @Override
    public List<ExamenResponseDTO> filterExamen(String matiere, String niveau, String statut) {
        return examenRepository.filterExamen(matiere, niveau, statut)
                .stream()
                .map(ExamenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamenResponseDTO> sortExamens(List<Examen> examens, String sortBy, String direction) {
        Comparator<Examen> comparator;

        switch (sortBy) {
            case "titre":
                comparator = Comparator.comparing(Examen::getTitre);
                break;

            case "dateExamen":
                comparator = Comparator.comparing(Examen::getDateExamen);
                break;

            case "niveau":
                comparator = Comparator.comparing(Examen::getNiveau);
                break;

            default:
                comparator = Comparator.comparing(Examen::getId);
        }

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return examens.stream()
                .sorted(comparator)
                .map(ExamenMapper::toDto)
                .collect(Collectors.toList());
    }
}
