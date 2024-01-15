package io.rewardsapp.service;

import io.rewardsapp.enums.VerificationType;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType);

    @Async
    void sendInactiveUserEmail(String firstName, String email);
}
