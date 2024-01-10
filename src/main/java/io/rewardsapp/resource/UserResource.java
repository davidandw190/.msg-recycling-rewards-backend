package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserPrincipal;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.*;
import io.rewardsapp.provider.TokenProvider;
import io.rewardsapp.service.RoleService;
import io.rewardsapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;

import static io.rewardsapp.dto.mapper.UserDTOMapper.toUser;
import static io.rewardsapp.filter.CustomAuthorizationFilter.TOKEN_PREFIX;
import static io.rewardsapp.utils.ExceptionUtils.handleException;
import static io.rewardsapp.utils.UserUtils.getLoggedInUser;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@Slf4j
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

    /**
     * Authenticates a user by processing the login request.
     *
     * @param loginForm The {@code UserLoginForm} containing user credentials.
     * @return ResponseEntity with the authentication result.
     */
    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid UserLoginForm loginForm) {
        UserDTO user = authenticate(loginForm.email(), loginForm.password());
        return user.usingMfa() ? sendLoginVerificationCode(user) : sendLoginResponse(user);
    }

    /**
     * Registers a new user in the system.
     *
     * @param registrationForm  Registration form containing the user registration information.
     * @return ResponseEntity with the registration result.
     */
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpResponse> registerUser(@RequestBody @Valid UserRegistrationForm registrationForm) {
        UserDTO createdUser = userService.createUser(registrationForm);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", createdUser))
                        .message("Recycler Account created successfully!")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    /**
     * Retrieves the profile of the authenticated user.
     *
     * @return ResponseEntity containing the user profile response.
     */
    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(@AuthenticationPrincipal UserDTO authenticatedUser) {
        log.info(roleService.getRoles().toString());
        log.info(authenticatedUser.roleName() + "        " + authenticatedUser.permissions());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", authenticatedUser,
                                "roles", roleService.getRoles()))
                        .message("Profile retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
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
                        .message("Profile updated successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
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
                        .build()
        );
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
                        .message("Login successful!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
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
                        .build()
        );
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
                        .build()
        );
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
                        .build()
        );
    }


    /**
     * Updates the password for the authenticated user.
     *
     * @param authenticatedUser The authenticated user details obtained from the security context.
     * @param form              The {@code UpdatePasswordForm} containing the current password, new password and confirmation password.
     * @return ResponseEntity containing the password update response.
     */
    @PatchMapping("/update/password")
    public ResponseEntity<HttpResponse> updatePassword(@AuthenticationPrincipal UserDTO authenticatedUser, @RequestBody @Valid UpdateUserPasswordForm form) {
        userService.updatePassword(authenticatedUser.id(), form.currentPassword(), form.newPassword(), form.confirmPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUser(authenticatedUser.id()),
                                "roles", roleService.getRoles()))
                        .message("Password updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Updates the role of the authenticated user.
     *
     * @param authenticatedUser     The authenticated user details obtained from the security context.
     * @param roleName              The name of the role to be assigned to the user.
     * @return ResponseEntity containing the user role update response.
     */
    @PatchMapping("/update/role/{roleName}")
    public ResponseEntity<HttpResponse> updateUserRole(@AuthenticationPrincipal UserDTO authenticatedUser, @PathVariable("roleName") String roleName) {
        userService.updateUserRole(authenticatedUser.id(), roleName);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of(
                                "user", userService.getUser(authenticatedUser.id()),
                                "roles", roleService.getRoles()))
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Role updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Updates the account settings for the authenticated user.
     *
     * @param authenticatedUser     The authenticated user details obtained from the security context.
     * @param form                  The {@code UpdateAccountSettingsForm} containing the updated account settings.
     * @return ResponseEntity containing the account settings update response.
     */
    @PatchMapping("/update/settings")
    public ResponseEntity<HttpResponse> updateAccountSettings(@AuthenticationPrincipal UserDTO authenticatedUser, @RequestBody @Valid UpdateAccountSettingsForm form) {
        userService.updateAccountSettings(authenticatedUser.id(), form.enabled(), form.notLocked());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUser(authenticatedUser.id()),
                                "roles", roleService.getRoles()))
                        .message("Account settings updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Handles the request to toggle user email notifications for the authenticated user.
     *
     * @param authenticatedUser The authenticated user details obtained from the security context.
     * @return ResponseEntity containing the response to the notification toggle request.
     */
    @PatchMapping("/toggle-notifications")
    public ResponseEntity<HttpResponse> toggleUserNotifications(@AuthenticationPrincipal UserDTO authenticatedUser) {
        UserDTO user = userService.toggleNotifications(authenticatedUser.email());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "roles", roleService.getRoles()))
                        .message(user.notificationsEnabled() ? "Notifications have been enabled" : "Notifications have been disabled")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Toggles Multi-Factor Authentication (MFA) for the authenticated user.
     *
     * @param authenticatedUser     The authenticated user details obtained from the security context.
     * @return ResponseEntity containing the MFA toggle response.
     */
    @PatchMapping("/toggle-mfa")
    public ResponseEntity<HttpResponse> toggleMfa(@AuthenticationPrincipal UserDTO authenticatedUser) {
        UserDTO user = userService.toggleMfa(authenticatedUser.email());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of(
                                "user", user,
                                "roles", roleService.getRoles()))
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Multi-Factor Authentication updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Handles the request to update the profile picture for the authenticated user.
     *
     * @param authenticatedUser The authenticated user details obtained from the security context.
     * @param image              The new profile picture image file.
     * @return ResponseEntity containing the response to the profile picture update request.
     */
    @PatchMapping("/update/profile-pic")
    public ResponseEntity<HttpResponse> updateProfileImage(@AuthenticationPrincipal UserDTO authenticatedUser, @RequestParam("image") MultipartFile image) {
        userService.updateImage(authenticatedUser, image);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of(
                                "user", userService.getUser(authenticatedUser.id()),
                                "roles", roleService.getRoles()))
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Profile picture updated Successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    /**
     * Handles the request to get the profile image for a given file name.
     *
     * @param fileName The name of the profile image file.
     * @return The byte array representing the profile image.
     * @throws Exception If an error occurs while reading the image file.
     */
    @GetMapping(value = "/image/{fileName}", produces = IMAGE_PNG_VALUE)
    public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws Exception {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Downloads/images/" + fileName));
    }

    /**
     * Handles the request to refresh the authentication token.
     *
     * @param request The HttpServletRequest containing the current token.
     * @return ResponseEntity containing the response to the token refresh request.
     */
    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
        if (isHeaderAndTokenValid(request)) {
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserDTO user = userService.getUser(tokenProvider.getSubject(token, request));
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
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .reason("Refresh Token missing or invalid")
                            ._devMessage("Refresh token missing or invalid")
                            .status(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .build()
            );
        }
    }

    /**
     * Handles HTTP errors and returns an appropriate response.
     *
     * @param request The HttpServletRequest causing the error.
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
            return getLoggedInUser(authenticationManager.authenticate(unauthenticated(email, password)));

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
                        .build()
        );
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(
                toUser(userService.getUser(user.id())),
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
