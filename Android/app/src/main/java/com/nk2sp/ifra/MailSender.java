package com.nk2sp.ifra;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender extends javax.mail.Authenticator{
    private String fromEmail = "nk2sp0925@gmail.com"; //gmail
    private String password = "qtxc vpmv xvgz ednt"; // gmail 2차 비밀번호 16자리

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(fromEmail, password);
    }

    public void sendEmail(String toEmail, String num) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.setProperty("mail.transport.protocol", "smtp");
                props.setProperty("mail.host", "smtp.gmail.com");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.setProperty("mail.smtp.quitwait", "false");

                Session session = Session.getDefaultInstance(props, new MailSender());

                Message message = new MimeMessage(session);
                ((MimeMessage) message).setSender(new InternetAddress(fromEmail));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
                message.setSubject("인증번호");
                message.setText("인증번호 : " + num);

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }
}