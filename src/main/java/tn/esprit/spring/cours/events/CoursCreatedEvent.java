package tn.esprit.spring.cours.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Événement envoyé via RabbitMQ au service EXAMEN
 * 🟢 COMMUNICATION ASYNCHRONE
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long coursId;
    private String titre;
    private String categorie;
    private String niveau;
    private Long enseignantId;
    private Integer dureeHeures;

    public CoursCreatedEvent(Long coursId, String titre, String categorie) {
        this.coursId = coursId;
        this.titre = titre;
        this.categorie = categorie;
    }
}