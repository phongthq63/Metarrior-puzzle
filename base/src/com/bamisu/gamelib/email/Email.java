package com.bamisu.gamelib.email;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Create by Popeye on 9:23 AM, 4/29/2020
 */
public class Email {
    public String email;
    public String pass;

    private Session session;

    public Email() {
    }

    public Email(String email, String pass) {
        this.email = email;
        this.pass = pass;
        initConnection();
    }

    private void initConnection() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.mailgun.org");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");

        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, pass);
                    }
                });
    }

    public boolean sendMail(String toEmail, String subject, String body){
        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "content/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(email, "Metarrior"));
            msg.setReplyTo(InternetAddress.parse(pass, false));
            msg.setSubject(subject);
            msg.setContent(body, "text/html");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            Transport.send(msg);
            System.out.println("EMail Sent Successfully!!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
