package tn.comping.spring.examen.dto;

import lombok.*;
import java.time.LocalDateTime;

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