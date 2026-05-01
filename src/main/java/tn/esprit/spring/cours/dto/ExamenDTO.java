package tn.esprit.spring.cours.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO pour la communication ASYNCHRONE avec le service EXAMEN via RabbitMQ
 * Conforme à l'atelier : même DTO dans les deux microservices
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamenDTO {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateExamen;
    private Integer duree;
    private Double coefficient;
    private String niveau;
    private String matiere;
    private String statut;
    private Long coursId;        // Liaison avec le cours
    private String coursTitre;   // Titre du cours associé
}