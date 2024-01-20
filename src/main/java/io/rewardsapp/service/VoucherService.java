package io.rewardsapp.service;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.vouchers.Voucher;
import io.rewardsapp.domain.vouchers.VoucherType;
import io.rewardsapp.dto.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;
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

    int checkForEarnedVouchers(User user, long rewardsPointsBeforeActivity);

    List<VoucherType> getVoucherTypes();
}
