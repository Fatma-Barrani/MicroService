package tn.comping.spring.examen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long coursId;
    private String titre;
    private String categorie;
    private String niveau;
    private Long enseignantId;
    private Integer dureeHeures;
}