package com.cts.identityauth.service.impl;

import com.cts.identityauth.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)   // ← this is the fix
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (mailSender == null) {
            // Mail not configured yet — just print to console
            System.out.println("[EMAIL] To: " + to);
            System.out.println("[EMAIL] Subject: " + subject);
            System.out.println("[EMAIL] Body: " + body);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("[EmailService] Failed: " + e.getMessage());
        }
    }
}