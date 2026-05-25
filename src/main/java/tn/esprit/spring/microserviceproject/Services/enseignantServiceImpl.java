package tn.esprit.spring.microserviceproject.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import tn.esprit.spring.microserviceproject.Dtos.EtudiantDto;
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
    private final EmailService emailService;
    private final EtudiantClient etudiantClient;

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

    private String mapMatiereToFiliere(String matiere) {

        switch (matiere.toLowerCase()) {
            case "java":
            case "spring":
            case "microservices":
                return "informatique";

            case "accounting":
                return "finance";

            case "marketing":
                return "business";

            default:
                throw new RuntimeException("No filiere mapped for matiere: " + matiere);
        }
    }

    // ============================================================
    // ⭐ SYNCHRONOUS METHOD (UPDATED)
    // Assign exam + fetch students + trigger async email sending
    // ============================================================
    @Override
    public enseignantResponseDto assignExamen(Long enseignantId, Long examenId) {

        enseignant ens;
        ExamenDto examen;
        List<EtudiantDto> etudiants = new ArrayList<>();

        try {

            // 1️⃣ Get teacher
            ens = repository.findById(enseignantId)
                    .orElseThrow(() -> new RuntimeException("Enseignant not found"));

            // 2️⃣ Get exam from Examen MS
            examen = examenClient.getExamenById(examenId);

            if (examen == null) {
                throw new RuntimeException("Examen not found");
            }

            // 3️⃣ Save exam in teacher
            if (ens.getExamensIds() == null) {
                ens.setExamensIds(new ArrayList<>());
            }

            ens.getExamensIds().add(examenId);
            repository.save(ens);

            // =====================================================
            // 4️⃣ FIXED: MATIERE → FILIERE mapping
            // =====================================================
            try {
                String matiere = examen.getMatiere();

                if (matiere != null && !matiere.isBlank()) {

                    String filiere = mapMatiereToFiliere(matiere.trim().toLowerCase());

                    System.out.println("👉 Matiere: " + matiere);
                    System.out.println("👉 Filiere mapped: " + filiere);

                    etudiants = etudiantClient.getByFiliere(filiere);

                } else {
                    System.out.println("⚠️ Matiere is null or empty");
                }

            } catch (Exception e) {
                System.out.println("⚠️ Etudiant service failed: " + e.getMessage());
            }

            // =====================================================
            // 5️⃣ Send emails only if students exist
            // =====================================================
            if (!etudiants.isEmpty()) {
                emailService.sendExamNotification(etudiants, examen, ens);
            } else {
                System.out.println("⚠️ No students found → skipping emails");
            }

            System.out.println("✅ Exam assigned successfully");

            return mapper.toDTO(ens);

        } catch (Exception e) {

            System.out.println("❌ assignExamen failed: " + e.getMessage());

            enseignant fallback = repository.findById(enseignantId)
                    .orElseThrow(() -> new RuntimeException("Enseignant not found"));

            return mapper.toDTO(fallback);
        }
    }

    // ============================================
    // 🟢 ASYNCHRONE (RabbitMQ - NEW)
    // ============================================
    public void assignExamenAsync(Long enseignantId, Long examenId) {

        AssignExamenEvent event = new AssignExamenEvent(enseignantId, examenId);

        examenProducer.sendAssignExamen(event);
    }
     //filtre
    @Override
    public enseignantResponseDto getEnseignantByExamen(Long examenId) {

        enseignant ens = repository.findAll()
                .stream()
                .filter(e -> e.getExamensIds() != null
                        && e.getExamensIds().contains(examenId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No enseignant found for examenId: " + examenId));

        return mapper.toDTO(ens);
    }

}