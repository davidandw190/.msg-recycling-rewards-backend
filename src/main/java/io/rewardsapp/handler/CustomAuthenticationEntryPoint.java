package io.rewardsapp.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.exception.ApiException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * The CustomAuthenticationEntryPoint class provides a customized implementation for handling authentication entry points.
 * It returns a JSON response indicating the unauthorized status with a timestamp and reason.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpResponse httpResponse = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason("Unauthorized. Please log in to access this resource.")
                .status(UNAUTHORIZED)
                .statusCode(UNAUTHORIZED.value())
                .build();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());

        try (OutputStream out = response.getOutputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, httpResponse);
            out.flush();

        } catch (IOException exception) {
            throw new ApiException("An issue occurred. Please try again later.");
        }
    }
}
