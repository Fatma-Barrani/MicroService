package tn.comping.spring.examen.Services;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.comping.spring.examen.events.AssignExamenEvent;
import tn.comping.spring.examen.events.CoursCreatedEvent;
import tn.comping.spring.examen.Services.ExamenService;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamenConsumer {

    private final ExamenService examenService;

    @RabbitListener(queues = "cours_queue")
    public void receiveCoursEvent(CoursCreatedEvent event) {
        log.info("📥 Réception événement: Nouveau cours créé - ID: {}, Titre: {}",
                event.getCoursId(), event.getTitre());
    }

    @RabbitListener(queues = "assign_examen_queue", ackMode = "MANUAL")
    public void receive(AssignExamenEvent event, Channel channel, Message message) {
        try {
            System.out.println("📩 EVENT RECEIVED FROM RABBITMQ");
            examenService.assignExamenToEnseignant(
                    event.getExamenId(),
                    event.getEnseignantId());
            System.out.println("📩 Message processed and acknowledged");
            // Acknowledge the message manually
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                // Optionally reject the message on failure
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ioException) {
                ioException.printStackTrace(); // handle failure of rejecting message
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}