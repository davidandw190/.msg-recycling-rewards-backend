package io.rewardsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static io.rewardsapp.constants.SecurityConstants.PASS_ENCODER_STRENGTH;

@SpringBootApplication
public class MsgRecyclingRewardsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsgRecyclingRewardsBackendApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(PASS_ENCODER_STRENGTH);
	}

}
