package tn.comping.spring.examen.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ExamenRequestDTO {
    private String titre;
    private String description;
    private LocalDateTime dateExamen;
    private Integer duree;
    private Double coefficient;
    private String niveau;
    private String matiere;
    private String statut;

}
