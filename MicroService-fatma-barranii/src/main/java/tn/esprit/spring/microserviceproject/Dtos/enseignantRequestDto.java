package tn.esprit.spring.microserviceproject.Dtos;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class enseignantRequestDto {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String specialite;
}
