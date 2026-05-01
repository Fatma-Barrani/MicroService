package tn.esprit.spring.cours.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String categorie;

    private Integer dureeHeures;

    private Integer nbPlaces;

    // Référence vers l'enseignant (service externe)
    @Column(name = "enseignant_id")
    private Long enseignantId;

    private String niveau;
}