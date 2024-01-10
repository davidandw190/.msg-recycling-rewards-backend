package io.rewardsapp.service;

import io.rewardsapp.enums.VerificationType;

public interface EmailService {
    void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType);
}
