package tn.esprit.spring.cours.repository;

import tn.esprit.spring.cours.entity.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {

    // ===== MÉTHODE MÉTIER DEMANDÉE =====
    List<Cours> findByCategorie(String categorie);

    // Méthodes supplémentaires utiles
    List<Cours> findByEnseignantId(Long enseignantId);
    List<Cours> findByNiveau(String niveau);
}