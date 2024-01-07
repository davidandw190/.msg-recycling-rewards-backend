package io.rewardsapp.service;

import io.rewardsapp.domain.User;
import io.rewardsapp.domain.Voucher;
import io.rewardsapp.dto.UserDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface VoucherService {
    Page<Voucher> searchVouchers(
            Long userId,
            String code,
            Optional<Boolean> redeemed,
            Optional<Boolean> expired,
            int page,
            int size,
            String sortBy,
            String sortOrder
    );

    int checkForUnretrievedVouchers(User user);

    Voucher getVoucher(Long userId, String voucherCode);

    Voucher redeemVoucher(UserDTO authenticatedUser, String voucherCode);

    boolean checkForEarnedVouchers(User user, long rewardsPointsBeforeActivity);

}
