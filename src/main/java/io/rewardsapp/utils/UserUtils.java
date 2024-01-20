package io.rewardsapp.utils;

import io.rewardsapp.domain.auth.UserPrincipal;
import io.rewardsapp.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

@Slf4j
public class UserUtils {
    public static UserDTO getAuthenticatedUser(Authentication authentication) {

        return ((UserDTO) authentication.getPrincipal());
    }

    public static UserDTO getLoggedInUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
