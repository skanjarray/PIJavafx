package com.esprit.ecotounsi.Config;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class MailSender {

    public static void sendEmail(String toEmail, String subject, String body) {
        // Propriétés pour la connexion au serveur SMTP (ici pour Gmail)
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Informations d'authentification (mon email et mon mot de passe)
        final String username = "maryemkaabachi43@gmail.com";
        final String password = "fwsj vate mdux uslx";

        // Création de la session de connexion avec le serveur SMTP
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // Envoi du message
            Transport.send(message);
            System.out.println("Email envoyé avec succès !");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Exemple d'envoi d'email
        sendEmail("fnaiech.ahmed1@gmail.com", "Test Email", "Ceci est un test d'email.");
    }
}
