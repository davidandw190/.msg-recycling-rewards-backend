package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserPrincipal;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.ResetForgottenPasswordForm;
import io.rewardsapp.form.UpdateUserDetailsForm;
import io.rewardsapp.form.UpdateUserPasswordForm;
import io.rewardsapp.form.UserLoginForm;
import io.rewardsapp.provider.TokenProvider;
import io.rewardsapp.service.RoleService;
import io.rewardsapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static io.rewardsapp.dto.mapper.UserDTOMapper.toUser;
import static io.rewardsapp.filter.CustomAuthorizationFilter.TOKEN_PREFIX;
import static io.rewardsapp.utils.ExceptionUtils.handleException;
import static io.rewardsapp.utils.UserUtils.getAuthenticatedUser;
import static io.rewardsapp.utils.UserUtils.getLoggedInUser;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserResource {

    private final UserService userService;
    private final RoleService roleService;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid UserLoginForm loginForm) {
        UserDTO user = authenticate(loginForm.email(), loginForm.password());
        return user.usingMfa() ? sendLoginVerificationCode(user) : sendLoginResponse(user);
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

    /**
     * Updates the details of the authenticated user.
     *
     * @param updateUserDetailsForm The {@code UpdateUserForm} containing updated user details.
     * @return ResponseEntity containing the updated user response.
     */
    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateUserDetailsForm updateUserDetailsForm) {
        UserDTO updatedUser = userService.updateUserDetails(updateUserDetailsForm);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", updatedUser,
                                "roles", roleService.getRoles()))
                        .message("User Updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    /**
     * Verifies the provided verification code for a user and enables the user in the app.
     *
     * @param key The key embedded in the account verification URL.
     * @return ResponseEntity containing the account verification response.
     */
    @GetMapping("/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message(userService.verifyAccountKey(key).enabled() ? "Account already verified" : "Account verified")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    /**
     * Verifies a user's account using the provided verification key.
     *
     * @param email The email of the user.
     * @param code The verification code.
     * @return ResponseEntity containing the verification response.
     */
    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        UserDTO user = userService.verifyCode(email, code);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    /**
     * Initiates the process of resetting a user's password and sends a confirmation email.
     *
     * @param email The email of the user for password reset.
     * @return ResponseEntity containing the password reset response.
     */
    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetForgottenPassword(@PathVariable("email") String email) {
        userService.resetForgottenPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Email sent. Please check your email to reset your password.")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    /**
     * Verifies a password reset URL and provides instructions to set a new password.
     *
     * @param key The key embedded in the password reset URL.
     * @return ResponseEntity containing the password verification response.
     */
    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyResetPasswordUrl(@PathVariable("key") String key) {
        UserDTO user = userService.verifyResetPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user))
                        .message("Please enter a new password")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    /**
     * Resets the user's password using the provided reset key.
     *
     * @param form The {@code ResetPasswordForm} containing the user ID, new password, and confirmation password.
     * @return ResponseEntity containing the password reset response.
     */
    @PutMapping("/new/password")
    public ResponseEntity<HttpResponse> resetPasswordWithKey(@RequestBody @Valid ResetForgottenPasswordForm form) {
        userService.updatePassword(form.userId(), form.password(), form.confirmPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Password reset successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }


    /**
     * Updates the password for the authenticated user.
     *
     * @param authentication The {@code Authentication} object containing user details.
     * @param form The {@code UpdatePasswordForm} containing the current password, new password, and confirmation password.
     * @return ResponseEntity containing the password update response.
     */
    @PatchMapping("/update/password")
    public ResponseEntity<HttpResponse> updatePassword(Authentication authentication, @RequestBody @Valid UpdateUserPasswordForm form) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updatePassword(userDTO.id(), form.currentPassword(), form.newPassword(), form.confirmPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.id()),
                                "roles", roleService.getRoles()
                        ))
                        .message("Password Updated Successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    /**
     * Updates the role of the authenticated user.
     *
     * @param authentication The {@code Authentication} object containing user details.
     * @param roleName The name of the role to be assigned to the user.
     * @return ResponseEntity containing the user role update response.
     */
    @PatchMapping("/update/role/{roleName}")
    public ResponseEntity<HttpResponse> updateUserRole(Authentication authentication, @PathVariable("roleName") String roleName) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateUserRole(userDTO.id(), roleName);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.id()),
                                "roles", roleService.getRoles()))
                        .timeStamp(now().toString())
                        .message("Role updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PatchMapping("/toggle-mfa")
    public ResponseEntity<HttpResponse> toggleMfa(Authentication authentication) throws InterruptedException {
        UserDTO user = userService.toggleMfa(getAuthenticatedUser(authentication).email());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of(
                                "user", user,
                                "roles", roleService.getRoles()))
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Multi-Factor Authentication updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }


    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
        if (isHeaderAndTokenValid(request)) {
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserDTO user = userService.getUserById(tokenProvider.getSubject(token, request));
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of(
                                    "user", user,
                                    "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                    "refresh_token", token))
                            .message("Token refreshed successfully")
                            .status(OK)
                            .statusCode(OK.value())
                            .build());
        } else {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .reason("Refresh Token missing or invalid")
                            ._devMessage("Refresh token missing or invalid")
                            .status(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .build());
        }
    }

    /**
     * Handles HTTP errors and returns an appropriate response.
     *
     * @param request The {@code HttpServletRequest} causing the error.
     * @return ResponseEntity containing the error response.
     */
    @RequestMapping("/user/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason("There is no mapping for a " + request.getMethod() + " request for this path on the server")
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .build(), NOT_FOUND);
    }

    private UserDTO authenticate(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            return getLoggedInUser(authentication);

        } catch (Exception exception) {
            handleException(request, response, exception);
            throw new ApiException(exception.getMessage());
        }
    }

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return  request.getHeader(AUTHORIZATION) != null &&
                request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) &&
                tokenProvider.isTokenValid(
                        tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request),
                        request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length())
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

    private ResponseEntity<HttpResponse> sendLoginVerificationCode(UserDTO user) {
        userService.sendAccountVerificationCode(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user))
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
