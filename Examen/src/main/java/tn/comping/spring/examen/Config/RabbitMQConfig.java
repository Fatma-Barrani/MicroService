package tn.comping.spring.examen.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ===============================
    // QUEUES
    // ===============================
    public static final String ASSIGN_EXAMEN_QUEUE = "assign_examen_queue";
    public static final String ETUDIANT_QUEUE = "etudiant.queue";

    // ===============================
    // EXCHANGES
    // ===============================
    public static final String EXCHANGE = "edunet.exchange"; // MODIFIÉ: aligné avec Etudiant
    public static final String ASSIGN_EXAMEN_EXCHANGE = "examen.exchange"; // AJOUTÉ: flux d'affectation async

    // ===============================
    // ROUTING KEYS
    // ===============================
    public static final String ASSIGN_EXAMEN_KEY = "examen.affecte"; // MODIFIÉ: aligné avec root service
    public static final String ETUDIANT_KEY = "etudiant.key"; // MODIFIÉ: aligné avec Etudiant

    // ===============================
    // QUEUES BEANS
    // ===============================
    @Bean
    public Queue assignExamenQueue() {
        return new Queue(ASSIGN_EXAMEN_QUEUE, true);
    }

    @Bean
    public Queue etudiantQueue() {
        return new Queue(ETUDIANT_QUEUE, true);
    }

    // ===============================
    // EXCHANGES
    // ===============================
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public TopicExchange assignExamenExchange() {
        return new TopicExchange(ASSIGN_EXAMEN_EXCHANGE);
    }

    // ===============================
    // BINDINGS
    // ===============================
    @Bean
    public Binding assignExamenBinding() {
        return BindingBuilder
                .bind(assignExamenQueue())
                .to(assignExamenExchange())
                .with(ASSIGN_EXAMEN_KEY);
    }

    @Bean
    public Binding etudiantBinding() {
        return BindingBuilder
                .bind(etudiantQueue())
                .to(exchange())
                .with(ETUDIANT_KEY);
    }

    // ===============================
    // JSON CONVERTER
    // ===============================
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ===============================
    // LISTENER FACTORY
    // ===============================
    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);

        return factory;
    }
}