package utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final boolean auth;
    private final boolean starttls;

    // Constructeur avec paramètres
    public EmailService(String username, String password, String host, int port, boolean auth, boolean starttls) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.auth = auth;
        this.starttls = starttls;
    }

    // Constructeur par défaut avec vos identifiants
    public EmailService() {
        this.username = "aziz.azizi@edu.isetcom.tn";
        this.password = "xrqwkardocrswhld";
        this.host = "smtp.gmail.com";
        this.port = 587;
        this.auth = true;
        this.starttls = true;
    }

    public boolean sendEmailWithAttachment(String to, String subject, String body, String attachmentPath, String attachmentName) {
        try {
            // Set up mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", auth);
            props.put("mail.smtp.starttls.enable", starttls);
            props.put("mail.smtp.ssl.trust", host);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            // Create a session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Enable debug mode for troubleshooting
            session.setDebug(true);

            // Create a message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Add attachment
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachmentPath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(attachmentName != null ? attachmentName : "attachment");
                multipart.addBodyPart(messageBodyPart);
            }

            // Set the content
            message.setContent(multipart);

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully to " + to);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode simplifiée pour envoyer un email sans pièce jointe
    public boolean sendEmail(String to, String subject, String body) {
        return sendEmailWithAttachment(to, subject, body, null, null);
    }
}
