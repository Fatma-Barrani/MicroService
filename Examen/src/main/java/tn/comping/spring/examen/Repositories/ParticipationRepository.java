package tn.comping.spring.examen.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.comping.spring.examen.Entites.Examen;
import tn.comping.spring.examen.Entites.Participation;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    Optional<Participation> findByEtudiantIdAndExamenId(Long etudiantId, Long examenId);
    List<Participation> findByExamenId(Long examenId);
    List<Participation> findByEtudiantId(Long etudiantId);
}
