package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.Voucher;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.service.UserService;
import io.rewardsapp.service.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static io.rewardsapp.dto.mapper.UserDTOMapper.toUser;
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

    /**
     * Searches for vouchers based on specified criteria and retrieves the results.
     *
     * @param authenticatedUser The authenticated user details.
     * @param code              Voucher code (optional).
     * @param page              Page number for pagination.
     * @param size              Page size for pagination.
     * @param sortBy            Sorting field (default: createdAt).
     * @param sortOrder         Sorting order (default: desc).
     * @param redeemed          Filter for redeemed vouchers (default: null, optional -> can be set True or False based on needs).
     * @param expired           Filter for expired vouchers (default: null, optional -> can be set True or False based on needs).
     * @return ResponseEntity with the search results.
     */
    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchVouchers(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam(defaultValue = "") String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,

            @RequestParam(required = false) Boolean redeemed,
            @RequestParam(required = false) Boolean expired
    ) {
        Map<String, Object> searchData = null;

        try {
            validatePageAndSize(page, size);


            searchData = Map.of(
                    "user", userService.getUser(authenticatedUser.id()),
                    "page", voucherService.searchVouchers(authenticatedUser.id(), code, Optional.ofNullable(redeemed), Optional.ofNullable(expired), page, size, sortBy, sortOrder
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

    /**
     * Retrieves details for a specific voucher.
     *
     * @param authenticatedUser The authenticated user details.
     * @param voucherCode       Code of the voucher.
     * @return ResponseEntity with the user and voucher details.
     */
    @GetMapping("/get/{code}")
    public ResponseEntity<HttpResponse> getVoucher(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @PathVariable("code") String voucherCode
    ) {
        User user = toUser(userService.getUser(authenticatedUser.id()));
        Voucher voucher = voucherService.getVoucher(authenticatedUser.id(), voucherCode);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "voucher", voucher))
                        .message("Voucher details retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Redeems a voucher for the authenticated user.
     *
     * @param authenticatedUser The authenticated user details.
     * @param voucherCode       Code of the voucher to redeem.
     * @return ResponseEntity with the user and redeemed voucher details.
     */
    @PostMapping("/redeem/{code}")
    public ResponseEntity<HttpResponse> redeemVoucher(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @PathVariable("code") String voucherCode
    ) {
        User user = toUser(userService.getUser(authenticatedUser.id()));
        Voucher voucher = voucherService.redeemVoucher(authenticatedUser, voucherCode);


        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "voucher", voucher))
                        .message("Voucher redeemed successfully!")
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
