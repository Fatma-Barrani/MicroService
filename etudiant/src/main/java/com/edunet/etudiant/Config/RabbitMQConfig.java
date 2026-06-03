package com.edunet.etudiant.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * 2 queues pour 2 communications asynchrones :
 *
 *  ASYNC 1 : etudiant.queue          → consommée par MS Examen
 *  ASYNC 2 : notif.enseignant.queue  → consommée par MS Enseignant  [NOUVELLE]
 */
@Configuration
public class RabbitMQConfig {

    // ── ASYNC 1 : Étudiant → Examen ──────────
    public static final String ETUDIANT_QUEUE = "etudiant.queue";

    // ── ASYNC 2 : Étudiant → Enseignant MS (NOUVELLE queue) ───────────────
    public static final String NOTIF_ENSEIGNANT_QUEUE = "notif.enseignant.queue";

    // ── Exchange commun ────────────────────────────────────────────────────
    public static final String EXCHANGE        = "edunet.exchange";
    public static final String ETUDIANT_KEY    = "etudiant.event";
    public static final String ENSEIGNANT_KEY  = "notif.enseignant";

    // ── Queue beans ────────────────────────────────────────────────────────
    @Bean
    public Queue etudiantQueue() {
        return new Queue(ETUDIANT_QUEUE, true);
    }

    @Bean
    public Queue notifEnseignantQueue() {
        return new Queue(NOTIF_ENSEIGNANT_QUEUE, true);
    }

    // ── Exchange bean ──────────────────────────────────────────────────────
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // ── Bindings ───────────────────────────────────────────────────────────
    @Bean
    public Binding etudiantBinding() {
        return BindingBuilder.bind(etudiantQueue()).to(exchange()).with(ETUDIANT_KEY);
    }

    @Bean
    public Binding enseignantBinding() {
        return BindingBuilder.bind(notifEnseignantQueue()).to(exchange()).with(ENSEIGNANT_KEY);
    }

    // ── JSON Converter ─────────────────────────────────────────────────────
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
