package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.Voucher;
import io.rewardsapp.repository.VoucherRepository;
import io.rewardsapp.service.VoucherService;
import io.rewardsapp.specs.VoucherSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;

    @Override
    public Page<Voucher> searchVouchers(
            Long userId,
            String code,
            Boolean redeemed,
            Boolean expired,
            int page,
            int size,
            String sortBy,
            String sortOrder
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Voucher> voucherSpecification = VoucherSpecification.searchVouchers(userId, code, redeemed, expired);

        return voucherRepository.findAll(voucherSpecification, pageable);
    }
}
