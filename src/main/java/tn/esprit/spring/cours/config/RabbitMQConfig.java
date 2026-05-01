package tn.esprit.spring.cours.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //COMMUNICATION ASYNCHRONE avec le service EXAMEN

    public static final String COURS_QUEUE = "cours_queue";

    @Bean //elle qui crée la queue dans RabbitMQ
    public Queue coursQueue() {
        return new Queue(COURS_QUEUE, true);
    }

    @Bean //Convertit CoursCreatedEvent en JSON
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean //Objet qui envoie les messages
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}