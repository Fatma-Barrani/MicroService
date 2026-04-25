package tn.esprit.spring.microserviceproject.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enseignants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class enseignant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String specialite;
}
