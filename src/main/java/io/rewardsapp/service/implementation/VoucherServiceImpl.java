package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.vouchers.Voucher;
import io.rewardsapp.domain.vouchers.VoucherType;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.exception.ApiException;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Service
@AllArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    // Services
    private final RewardPointsService rewardPointsService;

    // Repositories
    private final VoucherRepository voucherRepository;
    private final VoucherTypeRepository voucherTypeRepository;

    private static final int VOUCHER_LIFETIME_IN_DAYS = 30;

    /**
     * Searches for vouchers based on specified criteria and returns a paginated result.
     *
     * @param userId   The ID of the user performing the search.
     * @param code     Voucher code (optional).
     * @param redeemed Filter for redeemed vouchers (optional).
     * @param expired  Filter for expired vouchers (optional).
     * @param page     Page number for pagination.
     * @param size     Page size for pagination.
     * @param sortBy   Sorting field.
     * @param sortOrder Sorting order.
     * @return A paginated list of vouchers based on the search criteria.
     */
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

    /**
     * Checks and returns the count of unredeemed vouchers that are expired for a given user.
     *
     * @param user The user to check for unredeemed vouchers.
     * @return The count of unredeemed expired vouchers for the user.
     */
    @Override
    @Transactional
    public int checkForUnretrievedVouchers(User user) {
        return voucherRepository.countDistinctByUserIdAndRedeemedFalseAndExpiresAtIsBefore(user.getId(), LocalDateTime.now());
    }

    /**
     * Retrieves a voucher by its unique code, validating ownership by the specified user.
     *
     * @param userId      The ID of the user attempting to retrieve the voucher.
     * @param voucherCode The unique code of the voucher.
     * @return The retrieved voucher.
     * @throws ApiException If the voucher is not found or if the user doesn't own the voucher.
     */
    @Override
    @Transactional
    public Voucher getVoucher(Long userId, String voucherCode) {
        Voucher voucher = voucherRepository.findFirstByUniqueCode(voucherCode).orElseThrow(
                () -> new ApiException(">>>>> No voucher found by supplied unique code.")
        );

        if (!userOwnsVoucher(userId, voucher)) {
            throw new ApiException("No voucher found by supplied unique code.");
        }

        return voucher;
    }

    /**
     * Redeems a voucher for the authenticated user, updating its status and returning the redeemed voucher.
     *
     * @param authenticatedUser The authenticated user DTO.
     * @param voucherCode       The unique code of the voucher to redeem.
     * @return The redeemed voucher.
     * @throws ApiException If the voucher is not found, the user doesn't own the voucher, the voucher is already redeemed, or the voucher has expired.
     */
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

    /**
     * Checks for earned vouchers based on user activity and updates the user's vouchers accordingly.
     *
     * @param user                     The user for whom to check earned vouchers.
     * @param rewardsPointsBeforeActivity The user's rewards points balance before the activity.
     * @return The count of newly earned vouchers.
     */
    @Override
    @Transactional
    public int checkForEarnedVouchers(User user, long rewardsPointsBeforeActivity) {
        long rewardPointsAfterActivity = rewardPointsService.getRewardPointsAmount(user.getId());
        List<VoucherType> earnedVoucherTypes = voucherTypeRepository.findVoucherTypesByThresholdPointsBetween(rewardsPointsBeforeActivity, rewardPointsAfterActivity);

        if (!earnedVoucherTypes.isEmpty()) {
            List<Voucher> newVouchers = earnedVoucherTypes.stream()
                    .map(type -> buildVoucher(user, type))
                    .collect(Collectors.toList());

            List<Voucher> savedVouchers = voucherRepository.saveAll(newVouchers);
            return savedVouchers.size();
        }

        return 0;
    }

    @Override
    public List<VoucherType> getVoucherTypes() {
        return voucherTypeRepository.findAll();
    }

    /**
     * Checks if a user owns a particular voucher.
     *
     * @param userId  The ID of the user.
     * @param voucher The voucher to check ownership for.
     * @return True if the user owns the voucher, false otherwise.
     */
    private boolean userOwnsVoucher(Long userId, Voucher voucher) {
        return voucher.getUser().getId().equals(userId);
    }

    /**
     * Builds and returns a new voucher for the specified user and voucher type.
     *
     * @param user The user for whom to create the voucher.
     * @param type The voucher type.
     * @return The newly created voucher.
     */
    private Voucher buildVoucher(User user, VoucherType type) {
        return Voucher.builder()
                .voucherType(type)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(VOUCHER_LIFETIME_IN_DAYS))
                .uniqueCode(generateValidUniqueCode())
                .build();
    }

    /**
     * Generates a valid unique code for a voucher, ensuring uniqueness in the repository.
     *
     * @return A valid unique code for a voucher.
     */
    private String generateValidUniqueCode() {
        String generatedUniqueCode;

        do {
            generatedUniqueCode = randomAlphanumeric(8).toUpperCase();;
        } while (voucherRepository.existsByUniqueCode(generatedUniqueCode));

        return generatedUniqueCode;
    }

}
