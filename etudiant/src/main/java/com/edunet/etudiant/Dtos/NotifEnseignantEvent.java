package com.edunet.etudiant.Dtos;

import lombok.*;
import java.io.Serializable;

/**
 * Event envoyé via RabbitMQ queue "notif.enseignant.queue"
 * Consommé par le MS Enseignant de Fatma (EtudiantNotifConsumer)
 *
 * Scénario ASYNC 2 :
 * Quand un étudiant est INSCRIT ou sa MOYENNE EST MISE À JOUR,
 * l'enseignant responsable de sa filière est notifié automatiquement.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotifEnseignantEvent implements Serializable {
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantEmail;
    private String filiere;
    private Double moyenneGenerale;
    private String action;   // "INSCRIPTION" ou "MISE_A_JOUR_NOTE"
}
