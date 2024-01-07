package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.User;
import io.rewardsapp.domain.Voucher;
import io.rewardsapp.domain.VoucherType;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.repository.RewardPointsRepository;
import io.rewardsapp.repository.VoucherRepository;
import io.rewardsapp.repository.VoucherTypeRepository;
import io.rewardsapp.service.RewardPointsService;
import io.rewardsapp.service.VoucherService;
import io.rewardsapp.specs.VoucherSpecification;
import jakarta.transaction.Transactional;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Service
@AllArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final RewardPointsService rewardPointsService;
    private final RewardPointsRepository rewardPointsRepository;
    private final VoucherTypeRepository voucherTypeRepository;

    @Override
    public Page<Voucher> searchVouchers(
            Long userId,
            String code,
            Optional<Boolean> redeemed,
            Optional<Boolean> expired,
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

    @Override
    public Voucher getVoucher(Long userId, String voucherCode) {
        Voucher voucher = voucherRepository.findFirstByUniqueCode(voucherCode).orElseThrow(
                () -> new ApiException("No voucher found by supplied unique code.")
        );

        if (!userOwnsVoucher(userId, voucher)) {
            throw new ApiException("No voucher found by supplied unique code.");
        }

        return voucher;
    }

    @Override
    @Transactional
    public Voucher redeemVoucher(UserDTO authenticatedUser, String voucherCode) {
        Voucher voucher = getVoucher(authenticatedUser.id(), voucherCode);

        if (!userOwnsVoucher(authenticatedUser.id(), voucher)) {
            throw new ApiException("No voucher found by supplied unique code.");
        }

        if (voucher.isRedeemed()) {
            throw new ApiException("Voucher already redeemed.");
        }

        if (voucher.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Seems like you voucher has expired..");
        }

        voucher.setRedeemed(true);
        return voucherRepository.save(voucher);
    }

    @Override
    @Transactional
    public boolean checkForEarnedVouchers(User user, long rewardsPointsBeforeActivity) {

        long rewardPointsAfterActivity = rewardPointsService.getRewardPointsAmount(user.getId());
        List<VoucherType> earnedVoucherTypes = voucherTypeRepository.findVoucherTypesByThresholdPointsBetween(rewardsPointsBeforeActivity, rewardPointsAfterActivity);

        if (earnedVoucherTypes.isEmpty()) {
            return false;
        } else {
            for (VoucherType voucherType : earnedVoucherTypes) {
                voucherRepository.save(buildVoucher(user, voucherType));
            }

            return true;
        }

    }

    private boolean userOwnsVoucher(Long userId, Voucher voucher) {
        return voucher.getUser().getId().equals(userId);
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
