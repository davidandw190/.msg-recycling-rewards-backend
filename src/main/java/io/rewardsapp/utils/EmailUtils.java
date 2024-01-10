package io.rewardsapp.utils;

import io.rewardsapp.enums.VerificationType;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class EmailUtils {
    private final EmailService emailService;
    public void sendEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
//        CompletableFuture.runAsync(() -> emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType));

        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType);
            } catch (Exception exception) {
                throw new ApiException("Unable to send email");
            }
        });
    }

}
