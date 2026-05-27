package tn.comping.spring.examen.Services;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.comping.spring.examen.dto.EtudiantEventDTO;
import tn.comping.spring.examen.Config.RabbitMQConfig;

@Component
public class EtudiantConsumer {

   @RabbitListener(queues = RabbitMQConfig.ETUDIANT_QUEUE)
    public void receiveEtudiant(EtudiantEventDTO event) {
        System.out.println("📩 [EXAMEN] Étudiant reçu : " + event.getNom());
        System.out.println(" → Création automatique d’un dossier d'examen pour " + event.getPrenom() + " " + event.getNom());
    }
}