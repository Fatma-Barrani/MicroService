package tn.esprit.spring.microserviceproject.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tn.esprit.spring.microserviceproject.entities.enseignant;
import tn.esprit.spring.microserviceproject.Dtos.ExamenDto;
import tn.esprit.spring.microserviceproject.Dtos.EtudiantDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String SYSTEM_EMAIL = "fatmabarrani11@gmail.com";

    @Async
    @Override
    public void sendExamNotification(List<EtudiantDto> etudiants,
                                     ExamenDto examen,
                                     enseignant ens) {

        log.info("📧 Starting async email sending to {} students...", etudiants.size());

        for (EtudiantDto e : etudiants) {
            try {
                sendSingleEmail(e, examen, ens);
            } catch (Exception ex) {
                log.error("❌ Failed to send email to " + e.getEmail(), ex);
            }
        }

        log.info("✅ All exam emails processed!");
    }

    private void sendSingleEmail(EtudiantDto e, ExamenDto examen, enseignant ens) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(SYSTEM_EMAIL);
        message.setTo(e.getEmail());
        message.setReplyTo(ens.getEmail());

        message.setSubject("📚 New Exam from " + ens.getNom() + " " + ens.getPrenom());

        message.setText(
                "Hello " + e.getNom() + " " + e.getPrenom() + ",\n\n" +

                "Your teacher " + ens.getNom() + " " + ens.getPrenom()
                + " has scheduled a new exam.\n\n" +

                "📌 EXAM DETAILS\n" +
                "---------------------------------\n" +
                "Title: " + examen.getTitre() + "\n" +
                "Subject: " + examen.getMatiere() + "\n" +
                "Date: " + examen.getDateExamen() + "\n" +
                "Duration: " + examen.getDuree() + " minutes\n\n" +

                "👨‍🏫 Teacher Contact\n" +
                "Email: " + ens.getEmail() + "\n" +
                "Phone: " + ens.getTelephone() + "\n\n" +

                "Good luck!\n\n" +
                "University Exam Platform"
        );

        try {
            mailSender.send(message);
            log.info("✉ Email successfully sent to {}", e.getEmail());
        } catch (Exception ex) {
            log.error("🔥 SMTP ERROR sending email to " + e.getEmail(), ex);
            throw ex;
        }
    }
}