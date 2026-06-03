package com.edunet.etudiant.Dtos;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Représente la réponse de POST /api/examens/participer (examen)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationDTO {
    private Long id;
    private Long etudiantId;
    private Long examenId;
    private Double note;
    private String commentaire;
    private LocalDateTime dateEvaluation;
}
