package com.edunet.etudiant.Utils;

import com.edunet.etudiant.Dtos.EtudiantRequestDTO;
import com.edunet.etudiant.Dtos.EtudiantResponseDTO;
import com.edunet.etudiant.Entities.Etudiant;
import org.springframework.stereotype.Component;

@Component
public class EtudiantMapper {

    // Convertir DTO Request -> Entité
    public Etudiant toEntity(EtudiantRequestDTO dto) {
        return new Etudiant(
                dto.getNom(),
                dto.getPrenom(),
                dto.getEmail(),
                dto.getFiliere(),
                dto.getAnneeInscription(),
                dto.getMoyenneGenerale()
        );
    }

    // Convertir Entité -> DTO Response
    public EtudiantResponseDTO toResponseDTO(Etudiant entity) {
        return EtudiantResponseDTO.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .email(entity.getEmail())
                .filiere(entity.getFiliere())
                .anneeInscription(entity.getAnneeInscription())
                .moyenneGenerale(entity.getMoyenneGenerale())
                .build();
    }

    // Mettre à jour une entité existante à partir d'un DTO Request
    public void updateEntityFromDto(EtudiantRequestDTO dto, Etudiant entity) {
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setEmail(dto.getEmail());
        entity.setFiliere(dto.getFiliere());
        entity.setAnneeInscription(dto.getAnneeInscription());
        entity.setMoyenneGenerale(dto.getMoyenneGenerale());
    }
}