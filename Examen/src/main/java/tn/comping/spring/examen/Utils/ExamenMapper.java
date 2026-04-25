package tn.comping.spring.examen.Utils;

import tn.comping.spring.examen.Entites.Examen;
import tn.comping.spring.examen.dto.ExamenRequestDTO;
import tn.comping.spring.examen.dto.ExamenResponseDTO;

public class ExamenMapper {
    public static Examen toEntity(ExamenRequestDTO dto) {
        return Examen.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .dateExamen(dto.getDateExamen())
                .duree(dto.getDuree())
                .coefficient(dto.getCoefficient())
                .niveau(dto.getNiveau())
                .matiere(dto.getMatiere())
                .statut(dto.getStatut())
                .build();
    }

    public static ExamenResponseDTO toDto(Examen examen) {
        ExamenResponseDTO dto = new ExamenResponseDTO();
        dto.setId(examen.getId());
        dto.setTitre(examen.getTitre());
        dto.setDescription(examen.getDescription());
        dto.setDateExamen(examen.getDateExamen());
        dto.setDuree(examen.getDuree());
        dto.setCoefficient(examen.getCoefficient());
        dto.setNiveau(examen.getNiveau());
        dto.setMatiere(examen.getMatiere());
        dto.setStatut(examen.getStatut());
        return dto;
    }
}
