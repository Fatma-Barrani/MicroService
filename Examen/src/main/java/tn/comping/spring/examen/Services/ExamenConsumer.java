package tn.comping.spring.examen.messaging;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.comping.spring.examen.Services.ExamenService;
import tn.comping.spring.examen.events.AssignExamenEvent;
import java.io.IOException; // <-- Add this import

@Component
@RequiredArgsConstructor
public class ExamenConsumer {

    private final ExamenService examenService;

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