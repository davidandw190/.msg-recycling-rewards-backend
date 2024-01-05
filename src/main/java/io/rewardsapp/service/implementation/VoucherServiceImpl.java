package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.User;
import io.rewardsapp.domain.Voucher;
import io.rewardsapp.domain.VoucherType;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

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

    @Override
    public int checkForUnretrievedVouchers(User user) {
        return voucherRepository.countDistinctByUserIdAndRedeemedFalseAndExpiresAtIsBefore(user.getId(), LocalDateTime.now());
    }

    private void createNewVouchers(User user, VoucherType... voucherTypes) {
        if (voucherTypes.length > 0) {
            List<Voucher> vouchers = Arrays.stream(voucherTypes)
                    .map(type -> buildVoucher(user, type))
                    .collect(Collectors.toList());

            voucherRepository.saveAll(vouchers);
        }
    }

    private Voucher buildVoucher(User user, VoucherType type) {
        return Voucher.builder()
                .voucherType(type)
                .user(user)
                .uniqueCode(generateValidUniqueCode())
                .build();
    }

    private String generateValidUniqueCode() {
        String generatedUniqueCode;

        do {
            generatedUniqueCode = randomAlphanumeric(8).toUpperCase();;
        } while (voucherRepository.existsByUniqueCode(generatedUniqueCode));

        return generatedUniqueCode;
    }

}
