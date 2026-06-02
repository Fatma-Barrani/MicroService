package tn.comping.spring.examen.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "examen")
public class Examen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private String description;

    private LocalDateTime dateExamen;

    private Integer duree;

    private Double coefficient;

    private String niveau;

    private String matiere;

    private String statut;



    private Long enseignantId;

}
