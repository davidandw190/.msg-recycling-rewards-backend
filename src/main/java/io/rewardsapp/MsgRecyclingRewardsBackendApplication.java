package io.rewardsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static io.rewardsapp.constants.SecurityConstants.PASS_ENCODER_STRENGTH;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
public class MsgRecyclingRewardsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsgRecyclingRewardsBackendApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(PASS_ENCODER_STRENGTH);
	}

}
