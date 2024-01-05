package io.rewardsapp.service;

import io.rewardsapp.domain.User;

public interface VoucherService {
    Object searchVouchers(Long id, String code, Boolean redeemed, Boolean expired, int page, int size, String sortBy, String sortOrder);

    int checkForUnretrievedVouchers(User user);
}
