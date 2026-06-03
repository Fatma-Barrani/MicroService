package tn.esprit.spring.microserviceproject.Services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * ✅ DONNER CE FICHIER À FATMA
 *
 * À placer dans le MS Enseignant :
 *   src/main/java/tn/esprit/spring/microserviceproject/Services/EtudiantNotifConsumer.java
 *
 * Et dans RabbitMQConfig de Fatma, ajouter :
 *   public static final String NOTIF_ENSEIGNANT_QUEUE = "notif.enseignant.queue";
 *
 *   @Bean
 *   public Queue notifEnseignantQueue() {
 *       return new Queue(NOTIF_ENSEIGNANT_QUEUE, true);
 *   }
 *
 * Ce consumer reçoit les notifications envoyées par le MS Étudiant
 * quand un étudiant est inscrit ou sa note est mise à jour.
 */
@Component
public class EtudiantNotifConsumer {

    @RabbitListener(queues = "notif.enseignant.queue")
    public void recevoirNotifEtudiant(Map<String, Object> event) {
        System.out.println("==========================================================");
        System.out.println("📩 [ENSEIGNANT MS] Notification reçue depuis MS Étudiant !");
        System.out.println(" → Étudiant  : " + event.get("etudiantNom") + " " + event.get("etudiantPrenom"));
        System.out.println(" → Email     : " + event.get("etudiantEmail"));
        System.out.println(" → Filière   : " + event.get("filiere"));
        System.out.println(" → Moyenne   : " + event.get("moyenneGenerale"));
        System.out.println(" → Action    : " + event.get("action"));
        System.out.println("==========================================================");
        // Ici Fatma peut : envoyer un email, sauvegarder en base, etc.
    }
}
