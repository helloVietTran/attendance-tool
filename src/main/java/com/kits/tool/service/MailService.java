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

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /**
     * Gửi email
     *
     * @param to           email người nhận
     * @param subject      tiêu đề email
     * @param templateName tên file Thymeleaf template
     * @param variables    Map dữ liệu để render template
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        String status = "SUCCESS";
        String errorMsg = null;

        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

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
            System.out.println("TODO: Save log in database");
        }
    }

    /**
     * Gửi email HTML từ DTO tự động convert sang Map
     *
     * @param to           email người nhận
     * @param subject      tiêu đề
     * @param templateName template Thymeleaf
     * @param dto          bất kỳ DTO nào
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Object dto) {
        // Convert DTO to Map
        Map<String, Object> variables = Arrays.stream(org.springframework.beans.BeanUtils
                        .getPropertyDescriptors(dto.getClass()))
                .filter(pd -> pd.getReadMethod() != null)
                .collect(Collectors.toMap(
                        PropertyDescriptor::getName,
                        pd -> {
                            try {
                                return pd.getReadMethod().invoke(dto);
                            } catch (Exception e) {
                                return null;
                            }
                        }
                ));

        sendHtmlEmail(to, subject, templateName, variables);
    }
}