package com.notification.service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "sangaremoutou.360@gmail.com"; // À remplacer par votre email Gmail
    private static final String EMAIL_PASSWORD = "tqlz whbg uuvu vqzu"; // À remplacer par votre mot de passe d'application

    private static Session session;

    static {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });
    }

    public static void envoyerEmail(String destinataire, String sujet, String message) {
        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(EMAIL_FROM));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            mimeMessage.setSubject(sujet);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
            System.out.println("Email envoyé avec succès à " + destinataire);
            
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email à " + destinataire + ": " + e.getMessage());
        }
    }
} 