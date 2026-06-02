package tn.esprit.spring.cours.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursDTO {
    private Long id;
    private String titre;
    private String description;
    private String categorie;
    private Integer dureeHeures;
    private Integer nbPlaces;
    private Long enseignantId;
    private String niveau;

    // ===== Infos venues du service ENSEIGNANT (via OpenFeign - SYNCHRONE) =====
    private String enseignantNom;
    private String enseignantPrenom;
    private String enseignantEmail;
    private String enseignantSpecialite;
}