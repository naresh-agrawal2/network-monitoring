package com.dish.auth.utils;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Utility {
	@Value("${spring.mail.username}")
	private String fromEmail;
	private final PasswordEncoder passwordEncoder;
	private static final String OTP_CHARS = "0123456789";
	private static final int OTP_LENGTH = 4;
	private final SecureRandom random = new SecureRandom();
	private final JavaMailSender javaMailSender;

	public String generateOtp() {
		StringBuilder otp = new StringBuilder(OTP_LENGTH);
		for (int i = 0; i < OTP_LENGTH; i++) {
			otp.append(OTP_CHARS.charAt(random.nextInt(OTP_CHARS.length())));
		}
		return otp.toString();
	}

	public void sendOtpEmail(String email, String otp) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(fromEmail);
			helper.setTo(email);
			helper.setSubject("Your OTP Code");
			helper.setText("Your OTP code is: " + otp);

			javaMailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send email", e);
		}
	}

	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	public boolean isPasswordMatch(String password, String storedPassword) {
		return passwordEncoder.matches(password, storedPassword);
	}
}
