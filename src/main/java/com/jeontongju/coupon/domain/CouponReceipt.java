package com.jeontongju.coupon.domain;

import com.jeontongju.coupon.domain.common.BaseEntity;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon_receipt")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CouponReceipt extends BaseEntity {

    @EmbeddedId
    private CouponReceiptId id;

    @Builder.Default
    private Boolean isUse = false;
}
