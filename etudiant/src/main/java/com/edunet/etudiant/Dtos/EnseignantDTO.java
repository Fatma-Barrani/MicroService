package com.edunet.etudiant.Dtos;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnseignantDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String specialite;
}
