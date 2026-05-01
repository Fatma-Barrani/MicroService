package tn.comping.spring.examen.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.comping.spring.examen.Entites.Examen;

import java.util.List;

public interface ExamenRepository extends JpaRepository<Examen, Long> {

    List<Examen> findByMatiere(String matiere);

    List<Examen> findByNiveau(String niveau);

}
