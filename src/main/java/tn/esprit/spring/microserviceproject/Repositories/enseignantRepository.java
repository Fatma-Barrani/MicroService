package tn.esprit.spring.microserviceproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.microserviceproject.entities.enseignant;

public interface enseignantRepository extends JpaRepository<enseignant, Long> {

}
