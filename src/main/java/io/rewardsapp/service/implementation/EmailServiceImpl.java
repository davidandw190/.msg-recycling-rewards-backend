package io.rewardsapp.service.implementation;

import io.rewardsapp.enums.VerificationType;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.service.EmailService;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String FROM;

    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String VERIFICATION_EMAIL_TEMPLATE = "verification-email-template";
    public static final String INACTIVITY_EMAIL_TEMPLATE = "inactivity-email-template";
    public static final String TEXT_HTML_ENCODING = "text/html";

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);

            helper.setPriority(1);
            helper.setSubject(".msgRecyclingRewards - " + StringUtils.capitalize(verificationType.getType()) + " Verification Email");
            helper.setFrom(FROM);
            helper.setTo(email);

            String emailContent = getVerificationEmailMessage(verificationType);

            Context context = new Context();
            context.setVariable("name", firstName);
            context.setVariable("url", verificationUrl);
            context.setVariable("emailContent", emailContent);
            context.setVariable("verificationType", verificationType.name());

            String text = templateEngine.process(VERIFICATION_EMAIL_TEMPLATE, context);

            MimeMultipart mimeMultipart = new MimeMultipart("related");

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, TEXT_HTML_ENCODING);
            mimeMultipart.addBodyPart(messageBodyPart);

//            BodyPart imageBodyPart = new MimeBodyPart();
//            DataSource dataSource = new FileDataSource(System.getProperty("user.home") + "/Downloads/images/dog.jpg");
//            imageBodyPart.setDataHandler(new DataHandler(dataSource));
//            imageBodyPart.setHeader("Content-ID", "<image>");
//            mimeMultipart.addBodyPart(imageBodyPart);

            message.setContent(mimeMultipart);

            mailSender.send(message);

            log.info("HTML Email with embedded files sent to {}", firstName);

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Async
    @Override
    public void sendInactiveUserEmail(String email, String firstName) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);

            helper.setPriority(2);
            helper.setSubject(".msgRecyclingRewards - Make A Change In The Environment");
            helper.setFrom(FROM);
            helper.setTo(email);

            String emailContent = getInactivityEmailMessage();

            Context context = new Context();
            context.setVariable("name", firstName);
            context.setVariable("emailContent", emailContent);

            String text = templateEngine.process(INACTIVITY_EMAIL_TEMPLATE, context);

            MimeMultipart mimeMultipart = new MimeMultipart("related");

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, TEXT_HTML_ENCODING);
            mimeMultipart.addBodyPart(messageBodyPart);

            message.setContent(mimeMultipart);

            mailSender.send(message);

            log.info("Inactive user notification email sent to {}", firstName);

        } catch (Exception exception) {
            log.error("Error sending inactive user notification email to {}: {}", firstName, exception.getMessage());
            throw new RuntimeException("Error sending inactive user notification email", exception);
        }
    }

    private String getInactivityEmailMessage() {
        return "<br/>We noticed that you haven't logged into your RecyclingRewards account for a week. "
                + "<p>Recycling plays a crucial role in building a sustainable future, and your participation makes a positive impact. Reconnect with us to explore the latest recycling challenges, community initiatives, and exciting rewards awaiting you.</p>"
                + "<p>Log in today to contribute to a greener environment and enjoy the benefits of recycling with RecyclingRewards. Your involvement is instrumental in creating a better world through responsible recycling practices.</p>";
    }

    private String getVerificationEmailMessage(VerificationType verificationType) {
        switch (verificationType) {
            case PASSWORD -> {
                return "<br/>We have received a request to reset your account password. Please click the link below to securely reset your password:<br/>";
            }
            case ACCOUNT -> {
                return "<br/>Your new account has been successfully created. To enable your account and start enjoying our services, please click the link below:<br/>";
            }
            default -> throw new ApiException("Unable to send email. Email type unknown");
        }
    }

    private MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }

    private String getContentId(String filename) {
        return "<" + filename + ">";
    }
}
