package com.kits.tool.Mail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
    // Hàm Ghi Log (Logger)
    private static final Logger logger = LogManager.getLogger(MailSender.class);

    public static boolean sendEmail(Session session, String fromEmail, String toEmail, String subject, String body) {
        logger.info("Bắt đầu gửi mail | To: {} | Subject: {}", toEmail, subject); // Ghi log bắt đầu

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject, "UTF-8");
            message.setText(body, "UTF-8");

            Transport.send(message);

            logger.info("Gửi mail THÀNH CÔNG tới {}", toEmail);
            return true;

        } catch (MessagingException mex) {
            logger.error("Gửi mail THẤT BẠI tới {} | Lỗi: {}", toEmail, mex.getMessage(), mex);
            return false;
        }
    }
}
