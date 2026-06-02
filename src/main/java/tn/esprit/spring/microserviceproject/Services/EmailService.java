package tn.esprit.spring.microserviceproject.Services;

import tn.esprit.spring.microserviceproject.Dtos.ExamenDto;
import tn.esprit.spring.microserviceproject.Dtos.EtudiantDto;
import tn.esprit.spring.microserviceproject.entities.enseignant;

import java.util.List;

public interface EmailService {

    void sendExamNotification(List<EtudiantDto> etudiants,
                              ExamenDto examen,
                              enseignant ens);
}