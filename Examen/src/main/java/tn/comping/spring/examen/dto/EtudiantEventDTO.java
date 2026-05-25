package tn.comping.spring.examen.dto;

import lombok.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EtudiantEventDTO implements Serializable {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String filiere;
    private Integer anneeInscription;
    private Double moyenneGenerale;
}