package com.edunet.etudiant.Services;

import com.edunet.etudiant.config.RabbitMQConfig;
import com.edunet.etudiant.Dtos.EtudiantEventDTO;
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

    public void sendEtudiantEvent(EtudiantEventDTO dto) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.ETUDIANT_QUEUE, dto);
        log.info("Événement étudiant envoyé : {}", dto.getNom());
    }
}