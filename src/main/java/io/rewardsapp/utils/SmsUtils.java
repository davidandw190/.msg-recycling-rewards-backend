package io.rewardsapp.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.twilio.rest.api.v2010.account.Message.creator;

@Component
public class SmsUtils {
    @Value("${mfa.sms.from}")
    private static String FROM_NUMBER;

    @Value("${mfa.sms.sid-key}")
    private static String SID_KEY;

    @Value("${mfa.sms.token-key}")
    private static String TOKEN_KEY;

    public static void sendSMS(String to, String messageBody) {
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = creator(new PhoneNumber("+40" + to), new PhoneNumber(FROM_NUMBER), messageBody).create();
    }
}