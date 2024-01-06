package io.rewardsapp.service;

import io.rewardsapp.domain.User;
import io.rewardsapp.domain.Voucher;
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
}
