package io.rewardsapp.repository;

import io.rewardsapp.domain.vouchers.VoucherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherTypeRepository extends JpaRepository<VoucherType, Long> {

    List<VoucherType> findVoucherTypesByThresholdPointsBetween(long currentRewardPoints, long rewardPointsAfter);

    @Query("SELECT vt.thresholdPoints FROM VoucherType vt WHERE vt.thresholdPoints > :currentRewardPoints ORDER BY vt.thresholdPoints LIMIT 1")
    Optional<Long> findThresholdForNextRedeemableVoucher(Long currentRewardPoints);
}
