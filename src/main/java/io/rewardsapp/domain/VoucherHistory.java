package io.rewardsapp.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "voucher_history")
public class VoucherHistory {
    @Id
    @OneToOne
    @JoinColumn(name = "voucher_id", nullable = false, unique = true)
    private Voucher voucher;


    @Column(name = "redeem_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime redeemDate;
}