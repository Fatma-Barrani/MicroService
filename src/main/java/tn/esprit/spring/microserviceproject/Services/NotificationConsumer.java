package tn.esprit.spring.microserviceproject.Services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.esprit.spring.microserviceproject.Dtos.ExamEvent;

import java.util.Map;

@Component
public class NotificationConsumer {
    @RabbitListener(queues = "examen.queue")
    public void recevoir(ExamEvent event) {

        System.out.println("📩 Event reçu : ");
        System.out.println("Exam ID: " + event.getExamId());
        System.out.println("Teacher ID: " + event.getTeacherId());
        System.out.println("📩 MESSAGE REÇU !");
    }
}
