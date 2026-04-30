package tn.esprit.spring.microserviceproject.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.spring.microserviceproject.Config.RabbitMQConfig;
import tn.esprit.spring.microserviceproject.events.AssignExamenEvent;

@Service
@RequiredArgsConstructor
public class ExamenProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendAssignExamen(AssignExamenEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}