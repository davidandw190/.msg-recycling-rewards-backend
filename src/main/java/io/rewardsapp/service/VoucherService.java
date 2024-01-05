package io.rewardsapp.service;

public interface VoucherService {
    Object searchVouchers(Long id, String code, Boolean redeemed, Boolean expired, int page, int size, String sortBy, String sortOrder);
}
