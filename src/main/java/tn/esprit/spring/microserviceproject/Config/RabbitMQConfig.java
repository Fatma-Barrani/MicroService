package tn.esprit.spring.microserviceproject.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "assign_examen_queue";
    public static final String EXCHANGE = "examen.exchange"; // MODIFIÉ: aligné avec MS Examen
    public static final String ROUTING_KEY = "examen.affecte"; // MODIFIÉ: aligné avec MS Examen

    // AJOUTÉ: queue et binding pour notifications envoyées par le MS Etudiant
    public static final String NOTIF_ENSEIGNANT_QUEUE = "notif.enseignant.queue"; // AJOUTÉ
    public static final String EDUNET_EXCHANGE = "edunet.exchange"; // AJOUTÉ: exchange partagé
    public static final String NOTIF_ENSEIGNANT_KEY = "notif.enseignant"; // AJOUTÉ

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    // AJOUTÉ: queue pour recevoir les notifications provenant du MS Etudiant
    @Bean
    public Queue notifEnseignantQueue() {
        return new Queue(NOTIF_ENSEIGNANT_QUEUE, true);
    }

    // AJOUTÉ: exchange partagé utilisé par le MS Etudiant
    @Bean
    public TopicExchange edunetExchange() {
        return new TopicExchange(EDUNET_EXCHANGE);
    }

    // AJOUTÉ: binding pour la notification enseignant
    @Bean
    public Binding notifEnseignantBinding() {
        return BindingBuilder.bind(notifEnseignantQueue())
                .to(edunetExchange())
                .with(NOTIF_ENSEIGNANT_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}