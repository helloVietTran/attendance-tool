package com.kits.tool.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final LogService logService;

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        String status = "SUCCESS";
        String errorMsg = null;

        try {
            // 1. Chuẩn bị Context và Render HTML (Giữ nguyên)
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            // 2. Tạo và Gửi Mail Message (Giữ nguyên)
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@yourcompany.com");

            mailSender.send(message);

        } catch (MessagingException | RuntimeException e) {
            status = "FAILED";
            errorMsg = e.getMessage();
        } finally {
            // GỌI LOG: Lưu vào DB
            logService.saveLog(
                    "SEND_MAIL",      // Type
                    to,               // Target
                    "Subject: " + subject, // Description
                    status,           // Status
                    errorMsg          // Error
            );
        }
    }
}