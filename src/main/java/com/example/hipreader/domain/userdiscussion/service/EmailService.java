package com.example.hipreader.domain.userdiscussion.service;

import javax.naming.Context;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;

	// 기본 텍스트 메일 전송
	public void sendSimpleEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}

	// HTML 메일 전송 (Thymeleaf 템플릿 사용)
	public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

		try {
			String htmlContent = thymeleafTemplateEngine.process(templateName, context);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlContent, true);  // true: HTML 활성화
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("메일 전송 실패", e);
		}
	}
}
