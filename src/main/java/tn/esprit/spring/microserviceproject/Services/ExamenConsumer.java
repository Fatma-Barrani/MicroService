package tn.esprit.spring.microserviceproject.Services;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.esprit.spring.microserviceproject.Config.RabbitMQConfig;
import tn.esprit.spring.microserviceproject.events.AssignExamenEvent;

import java.io.IOException;

/**
 * AJOUTÉ: Consumer pour écouter les affectations d'examens asynchrones
 * Reçoit les événements publiés par le MS Examen vers `assign_examen_queue`
 * Cet événement contient l'examen ID et l'enseignant ID à assigner
 */
@Component
@RequiredArgsConstructor
public class ExamenConsumer {

    private final enseignantService service;

    /**
     * Écoute la queue `assign_examen_queue` sur l'exchange `examen.exchange`
     * avec la clé de routage `examen.affecte`
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE, ackMode = "MANUAL")
    public void receiveAssignExamen(AssignExamenEvent event, Channel channel, Message message) {
        try {
            System.out.println("📩 [ENSEIGNANT MS] Événement d'affectation reçu");
            System.out.println(" → Examen ID: " + event.getExamenId());
            System.out.println(" → Enseignant ID: " + event.getEnseignantId());

            // Appelle le service pour assigner l'examen à l'enseignant
            service.assignExamen(event.getEnseignantId(), event.getExamenId());

            System.out.println("✅ Examen assigné avec succès !");

            // Acknowledge le message
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du traitement de l'événement d'affectation: " + e.getMessage());
            e.printStackTrace();
            try {
                // Rejete le message en cas d'erreur (requeue si nécessaire)
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
