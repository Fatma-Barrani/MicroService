package tn.comping.spring.examen.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tn.comping.spring.examen.dto.ExamEvent;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamProducer {
    private final RabbitTemplate rabbitTemplate;


        public void sendTeacherAssignedEvent(Long examId, Long teacherId) {

            ExamEvent event = new ExamEvent(examId, teacherId);

            System.out.println("🔥 ENTER affecterEnseignant");

            rabbitTemplate.convertAndSend(
                    "examen.exchange",
                    "examen.affecte",
                    event
            );
            System.out.println("✅ Message envoyé vers RabbitMQ");
        }
}
