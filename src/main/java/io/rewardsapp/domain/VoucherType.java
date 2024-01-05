package io.rewardsapp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "/voucher_types")
public class VoucherType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_type_id")
    private Long voucherTypeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "threshold_points", nullable = false)
    private int thresholdPoints;
}