package com.jeontongju.coupon.mapper;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.domain.CouponReceipt;
import com.jeontongju.coupon.domain.CouponReceiptId;
import com.jeontongju.coupon.dto.response.CurCouponStatusForReceiveResponseDto;
import io.github.bitbox.bitbox.enums.CouponTypeEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CouponMapper {

  public Coupon toRegularPaymentsCouponEntity(String couponCode, LocalDateTime issuedAt) {

    LocalDateTime expiredAt = issuedAt.plusYears(1);

    return Coupon.builder()
        .couponCode(couponCode)
        .couponName(CouponTypeEnum.YANGBAN.name())
        .discountAmount(3000L)
        .issueLimit(1L)
        .issuedAt(issuedAt)
        .expiredAt(expiredAt)
        .minOrderPrice(15000L)
        .build();
  }

  public CouponReceipt toCouponReceiptEntity(Coupon coupon, Long consumerId) {

    CouponReceiptId build = CouponReceiptId.builder().coupon(coupon).consumerId(consumerId).build();
    return CouponReceipt.builder().id(build).isUse(false).build();
  }

  public CurCouponStatusForReceiveResponseDto toCurCouponStatusDto() {

    return CurCouponStatusForReceiveResponseDto.builder().isSoldOut(false).isOpen(true).build();
  }
}
