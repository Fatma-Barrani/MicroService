package com.edunet.etudiant.Entities;

import lombok.*;
import jakarta.persistence.*;

@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Getter
@Setter
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String nom;

    @NonNull
    private String prenom;

    @NonNull
    private String email;

    @NonNull
    private String filiere;

    @NonNull
    private Integer anneeInscription;

    @NonNull
    private Double moyenneGenerale;
}