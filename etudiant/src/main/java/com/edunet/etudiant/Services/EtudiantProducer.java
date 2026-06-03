package com.edunet.etudiant.Services;
import com.edunet.etudiant.Dtos.NotifEnseignantEvent;
import com.edunet.etudiant.Config.RabbitMQConfig;
import com.edunet.etudiant.Dtos.EtudiantEventDTO;
import com.edunet.etudiant.Dtos.NotifEnseignantEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EtudiantProducer {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(EtudiantProducer.class);

    public EtudiantProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     *  ASYNC 1 — Publier un événement étudiant vers  Examen
     * Déclenchement : à chaque addEtudiant() ou updateEtudiant()
     * Le MS Examen consomme ce message dans EtudiantConsumer et crée un dossier de participation
     */
    public void sendEtudiantEvent(EtudiantEventDTO dto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ETUDIANT_KEY,
                dto
        );
        log.info("📤 Événement étudiant publié : {} {} action={}",
                dto.getNom(), dto.getPrenom(), dto.getAction());
    }

    /**
     *  ASYNC 2 — Notifier  Enseignant d'un changement étudiant
     * Déclenchement : à chaque addEtudiant() (action=INSCRIPTION)
     *              ou updateEtudiant() si moyenne modifiée (action=MISE_A_JOUR_NOTE)
     * l'Enseignant consomme ce message dans EtudiantNotifConsumer
     */
    public void sendNotifEnseignant(NotifEnseignantEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ENSEIGNANT_KEY,
                event
        );
        log.info("📤  Notification envoyée : étudiant={} {} action={}",
                event.getEtudiantNom(), event.getEtudiantPrenom(), event.getAction());
    }
}