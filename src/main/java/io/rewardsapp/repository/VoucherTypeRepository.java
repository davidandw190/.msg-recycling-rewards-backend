package io.rewardsapp.repository;

import io.rewardsapp.domain.VoucherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherTypeRepository extends JpaRepository<VoucherType, Long> {

    List<VoucherType> findVoucherTypesByThresholdPointsBetween(long currentRewardPoints, long rewardPointsAfter);
}
