package io.rewardsapp.service.implementation;

import io.rewardsapp.enums.VerificationType;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private static String FROM;

    @Override
    public void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM);
            message.setTo(email);
            message.setText(getEmailMessage(firstName, verificationUrl, verificationType));
            message.setSubject(String.format(".msgRecyclingRewards - %s Verification Email", StringUtils.capitalize(verificationType.getType())));
            mailSender.send(message);
            log.info("Email sent to {}", firstName);

        } catch (Exception exception) {
            log.error(exception.getMessage());
        }

    }

    private String getEmailMessage(String firstName, String verificationUrl, VerificationType verificationType) {
        switch (verificationType) {
            case PASSWORD -> { return "Hello " + firstName + "\n\nReset password request. Please click the link below to reset your password. \n\n" + verificationUrl + "\n\nThe RecyclingRewards Support Team"; }
            case ACCOUNT -> { return "Hello " + firstName + "\n\nYour new account has been created. Please click the link below to verify and enable your account. \n\n" + verificationUrl + "\n\nThe RecyclingRewards Support Team"; }
            default -> throw new ApiException("Unable to send email. Email type unknown");
        }
    }
}
