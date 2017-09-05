package de.slg.startseite;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.slg.leoapp.List;

class MailClient {
    private final String       fromEmail;
    private final String       fromPassword;
    private final List<String> toEmailList;
    private final String       emailSubject;
    private final String       emailBody;

    private final Properties  emailProperties;
    private       Session     mailSession;
    private       MimeMessage emailMessage;

    MailClient(String fromEmail, String fromPassword,
               List<String> toEmailList, String emailSubject, String emailBody) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        emailProperties = System.getProperties();
        String emailPort = "587";
        emailProperties.put("mail.smtp.port", emailPort);
        String smtpAuth = "true";
        emailProperties.put("mail.smtp.auth", smtpAuth);
        String starttls = "true";
        emailProperties.put("mail.smtp.starttls.enable", starttls);
    }

    void createEmailMessage() throws
            MessagingException, UnsupportedEncodingException {
        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);
        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        for (String toEmail : toEmailList) {
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
        }
        emailMessage.setSubject(emailSubject);
        emailMessage.setContent(emailBody, "text/html");
    }

    void sendEmail() throws MessagingException {
        Transport transport = mailSession.getTransport("smtp");
        String    emailHost = "smtp.gmail.com";
        transport.connect(emailHost, fromEmail, fromPassword);
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
    }
}

