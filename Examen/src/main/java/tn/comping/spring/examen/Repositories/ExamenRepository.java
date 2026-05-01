package tn.comping.spring.examen.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.comping.spring.examen.Entites.Examen;

import java.util.List;

public interface ExamenRepository extends JpaRepository<Examen, Long> {

    List<Examen> findByMatiere(String matiere);

    List<Examen> findByNiveau(String niveau);
    @Query("""
    SELECT e FROM Examen e
    WHERE (:matiere IS NULL OR e.matiere = :matiere)
      AND (:niveau IS NULL OR e.niveau = :niveau)
      AND (:statut IS NULL OR e.statut = :statut)
""")
    List<Examen> filterExamen(
            @Param("matiere") String matiere,
            @Param("niveau") String niveau,
            @Param("statut") String statut
    );

}
