package com.edunet.etudiant.Dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtudiantResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String filiere;
    private Integer anneeInscription;
    private Double moyenneGenerale;
}