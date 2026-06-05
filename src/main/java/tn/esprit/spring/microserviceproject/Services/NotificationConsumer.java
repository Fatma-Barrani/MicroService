package tn.esprit.spring.microserviceproject.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import tn.esprit.spring.microserviceproject.Config.RabbitMQConfig;
import tn.esprit.spring.microserviceproject.events.AssignExamenEvent;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final enseignantService enseignantService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void recevoir(AssignExamenEvent event) {
        System.out.println("📩 AssignExamenEvent reçu : ");
        System.out.println("Enseignant ID: " + event.getEnseignantId());
        System.out.println("Examen ID: " + event.getExamenId());

        try {
            enseignantService.assignExamen(event.getEnseignantId(), event.getExamenId());
            System.out.println("✅ Affectation traitée avec succès via RabbitMQ");
        } catch (Exception e) {
            System.out.println("❌ Échec du traitement RabbitMQ : " + e.getMessage());
            throw e;
        }
    }
}
