package tn.esprit.spring.cours.service;

import tn.esprit.spring.cours.client.EnseignantClient;
import tn.esprit.spring.cours.dto.CoursDTO;
import tn.esprit.spring.cours.dto.EnseignantDTO;
import tn.esprit.spring.cours.entity.Cours;
import tn.esprit.spring.cours.repository.CoursRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoursService {

    private final CoursRepository coursRepository;
    private final EnseignantClient enseignantClient;  // 🔵 SYNCHRONE
    private final RabbitMQProducer rabbitProducer;    // 🟢 ASYNCHRONE

    // CREATE
    public CoursDTO createCours(CoursDTO dto) {
        log.info("📚 Création du cours: {}", dto.getTitre());

        Cours cours = mapToEntity(dto);
        Cours saved = coursRepository.save(cours);

        // 🟢 Envoi asynchrone vers le service EXAMEN
        rabbitProducer.sendCoursCreatedEvent(saved);

        return mapToDTO(saved);
    }

    // READ ALL
    public List<CoursDTO> getAllCours() {
        return coursRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public CoursDTO getCoursById(Long id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        return mapToDTO(cours);
    }

    // UPDATE
    public CoursDTO updateCours(Long id, CoursDTO dto) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        cours.setTitre(dto.getTitre());
        cours.setDescription(dto.getDescription());
        cours.setCategorie(dto.getCategorie());
        cours.setDureeHeures(dto.getDureeHeures());
        cours.setNbPlaces(dto.getNbPlaces());
        cours.setEnseignantId(dto.getEnseignantId());
        cours.setNiveau(dto.getNiveau());

        Cours updated = coursRepository.save(cours);
        return mapToDTO(updated);
    }

    // DELETE
    public void deleteCours(Long id) {
        coursRepository.deleteById(id);
        log.info("🗑️ Cours supprimé ID: {}", id);
    }

    // ===== MÉTHODE MÉTIER =====
    public List<CoursDTO> rechercherParCategorie(String categorie) {
        log.info("🔍 Recherche des cours par catégorie: {}", categorie);
        return coursRepository.findByCategorie(categorie).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ===== Mapper Entity -> DTO avec appel SYNCHRONE vers Enseignant =====
    private CoursDTO mapToDTO(Cours cours) {
        CoursDTO dto = CoursDTO.builder()
                .id(cours.getId())
                .titre(cours.getTitre())
                .description(cours.getDescription())
                .categorie(cours.getCategorie())
                .dureeHeures(cours.getDureeHeures())
                .nbPlaces(cours.getNbPlaces())
                .enseignantId(cours.getEnseignantId())
                .niveau(cours.getNiveau())
                .build();

        // 🔵 Appel SYNCHRONE vers le service ENSEIGNANT via OpenFeign
        if (cours.getEnseignantId() != null) {
            try {
                EnseignantDTO enseignant = enseignantClient.getEnseignantById(cours.getEnseignantId());
                dto.setEnseignantNom(enseignant.getNom());
                dto.setEnseignantPrenom(enseignant.getPrenom());
                dto.setEnseignantEmail(enseignant.getEmail());
                dto.setEnseignantSpecialite(enseignant.getSpecialite());
                log.debug("✅ [OPENFEIGN - SYNCHRONE] Infos enseignant récupérées pour ID: {}", cours.getEnseignantId());
            } catch (Exception e) {
                log.warn("⚠️ [OPENFEIGN - SYNCHRONE] Impossible de récupérer l'enseignant ID: {}", cours.getEnseignantId());
            }
        }

        return dto;
    }

    private Cours mapToEntity(CoursDTO dto) {
        return Cours.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .categorie(dto.getCategorie())
                .dureeHeures(dto.getDureeHeures())
                .nbPlaces(dto.getNbPlaces())
                .enseignantId(dto.getEnseignantId())
                .niveau(dto.getNiveau())
                .build();
    }
}