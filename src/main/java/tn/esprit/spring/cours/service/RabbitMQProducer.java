package tn.esprit.spring.cours.service;

import tn.esprit.spring.cours.config.RabbitMQConfig;
import tn.esprit.spring.cours.entity.Cours;
import tn.esprit.spring.cours.events.CoursCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 🟢 COMMUNICATION ASYNCHRONE avec le service EXAMEN
 * Envoi de messages via RabbitMQ
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendCoursCreatedEvent(Cours cours) {
        CoursCreatedEvent event = new CoursCreatedEvent(
                cours.getId(),
                cours.getTitre(),
                cours.getCategorie()
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.COURS_QUEUE, event);
        log.info("📨 [RABBITMQ - ASYNCHRONE] Événement envoyé au service EXAMEN pour le cours: {}", cours.getTitre());
    }
}