package tn.comping.spring.examen.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
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
    // EXCHANGE
    // ===============================
    public static final String EXCHANGE = "examen_exchange";

    // ===============================
    // ROUTING KEYS
    // ===============================
    public static final String ASSIGN_EXAMEN_KEY = "assign_examen_key";
    public static final String ETUDIANT_KEY = "etudiant_key";

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
    // EXCHANGE
    // ===============================
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // ===============================
    // BINDINGS
    // ===============================
    @Bean
    public Binding assignExamenBinding() {
        return BindingBuilder
                .bind(assignExamenQueue())
                .to(exchange())
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