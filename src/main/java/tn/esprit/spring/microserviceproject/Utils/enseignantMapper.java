package tn.esprit.spring.microserviceproject.Utils;

import org.springframework.stereotype.Component;
import tn.esprit.spring.microserviceproject.Dtos.enseignantRequestDto;
import tn.esprit.spring.microserviceproject.Dtos.enseignantResponseDto;
import tn.esprit.spring.microserviceproject.entities.enseignant;

@Component
public class enseignantMapper {

    public enseignant toEntity(enseignantRequestDto dto) {
        return enseignant.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .telephone(dto.getTelephone())
                .specialite(dto.getSpecialite())
                .build();
    }

    public enseignantResponseDto toDTO(enseignant e) {
        return enseignantResponseDto.builder()
                .id(e.getId())
                .nom(e.getNom())
                .prenom(e.getPrenom())
                .email(e.getEmail())
                .telephone(e.getTelephone())
                .specialite(e.getSpecialite())
                .build();
    }

    public void updateEntityFromDto(enseignantRequestDto dto, enseignant e) {
        e.setNom(dto.getNom());
        e.setPrenom(dto.getPrenom());
        e.setEmail(dto.getEmail());
        e.setTelephone(dto.getTelephone());
        e.setSpecialite(dto.getSpecialite());
    }
}
