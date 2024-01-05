package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.service.UserService;
import io.rewardsapp.service.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

import static io.rewardsapp.utils.ExceptionUtils.handleException;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/vouchers")
@RequiredArgsConstructor
public class VoucherResource {
    private final UserService userService;
    private final VoucherService voucherService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchVouchers(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam(defaultValue = "") String code,
            @RequestParam(required = false) boolean redeemed,
            @RequestParam(required = false) boolean expired,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Map<String, Object> searchData = null;

        try {
            validatePageAndSize(page, size);

            searchData = Map.of(
                    "user", userService.getUser(authenticatedUser.id()),
                    "page", voucherService.searchVouchers(authenticatedUser.id(), code, redeemed, expired, page, size, sortBy, sortOrder
                    )
            );
        } catch (Exception exception) {
            handleException(request, response, exception);
        }

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(searchData)
                        .message("Vouchers retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    private void validatePageAndSize(int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new ApiException("Invalid page or size parameters");
        }
    }
}
