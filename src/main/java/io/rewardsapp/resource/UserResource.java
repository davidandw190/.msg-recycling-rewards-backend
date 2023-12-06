package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.UserLoginForm;
import io.rewardsapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserResource {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid UserLoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.email(), loginForm.password()));
        UserDTO authenticatedUser = getLoggedInUser(authentication);
        return authenticatedUser.usingMfa() ? sendVerificationCode(authenticatedUser) : sendResponse(authenticatedUser);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User newUser) {
        UserDTO createdUser = userService.createNewUser(newUser);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", createdUser))
                        .message("User created successfully!")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    private URI getUri() {
        return null;
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO authenticatedUser) {
        return null;
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO authenticatedUser) {
        return null;
    }

    private UserDTO getLoggedInUser(Authentication authentication) {
        return null;
    }
}
