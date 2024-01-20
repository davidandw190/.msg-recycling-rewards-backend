package io.rewardsapp.repository;

import io.rewardsapp.domain.vouchers.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VoucherRepository extends
        PagingAndSortingRepository<Voucher, Long>,
        ListCrudRepository<Voucher, Long>,
        JpaSpecificationExecutor<Voucher> {

    Page<Voucher> findByUniqueCodeContainingIgnoreCase(String uniqueCode, Pageable pageable);

    int countDistinctByUserIdAndRedeemedFalseAndExpiresAtIsBefore(Long userId, LocalDateTime currentTime);

    boolean existsByUniqueCode(String code);

    Optional<Voucher> findFirstByUniqueCode(String voucherCode);
}
