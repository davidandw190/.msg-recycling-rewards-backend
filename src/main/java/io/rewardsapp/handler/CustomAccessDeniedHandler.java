package io.rewardsapp.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.exception.ApiException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * The CustomAccessDeniedHandler class provides a customized implementation for handling access denied situations.
 * It returns a custom JSON response indicating the forbidden status with a timestamp and reason.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpResponse httpResponse = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason("Access denied. Insufficient permissions to access this resource.")
                .status(FORBIDDEN)
                .statusCode(FORBIDDEN.value())
                .build();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());

        try (OutputStream out = response.getOutputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, httpResponse);
            out.flush();

        } catch (IOException exception) {
            throw new ApiException("An issue occurred. Please try again later.");
        }
    }
}