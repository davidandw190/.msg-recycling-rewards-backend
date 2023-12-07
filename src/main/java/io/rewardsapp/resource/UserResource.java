package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserPrincipal;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.UserLoginForm;
import io.rewardsapp.provider.TokenProvider;
import io.rewardsapp.service.RoleService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static io.rewardsapp.dto.mapper.UserDTOMapper.toUser;
import static io.rewardsapp.utils.UserUtils.getLoggedInUser;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserResource {
    
    private final UserService userService;
    private final RoleService roleService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> loginUser(@RequestBody @Valid UserLoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.email(), loginForm.password()));
        UserDTO authenticatedUser = getLoggedInUser(authentication);
        return authenticatedUser.usingMfa()
                ? sendLoginVerificationCode(authenticatedUser)
                : sendLoginResponse(authenticatedUser);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> registerUser(@RequestBody @Valid User newUser) {
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



    private ResponseEntity<HttpResponse> sendLoginResponse(UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(
                toUser(userService.getUserByEmail(user.email())),
                roleService.getRoleByUserId(user.id())
        );
    }

    private ResponseEntity<HttpResponse> sendLoginVerificationCode(UserDTO authenticatedUser) {
        userService.sendAccountVerificationCode(authenticatedUser);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", authenticatedUser))
                        .message("Verification Code Sent")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }


    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }
}
