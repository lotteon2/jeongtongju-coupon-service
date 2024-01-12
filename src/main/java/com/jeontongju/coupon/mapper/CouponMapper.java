package com.jeontongju.coupon.mapper;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.domain.CouponReceipt;
import com.jeontongju.coupon.domain.CouponReceiptId;
import com.jeontongju.coupon.dto.response.AvailableCouponInfoForSummaryNDetailsResponseDto;
import com.jeontongju.coupon.dto.response.CouponInfoForSingleInquiryResponseDto;
import com.jeontongju.coupon.dto.response.CurCouponStatusForReceiveResponseDto;
import io.github.bitbox.bitbox.dto.SubscriptionCouponBenefitForInquiryResponseDto;
import io.github.bitbox.bitbox.enums.CouponTypeEnum;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

  public Coupon toWelcomeCouponEntity(String couponCode, LocalDateTime issuedAt) {

    LocalDateTime expiredAt = issuedAt.plusMonths(issuedAt.getMonth().getValue());

    return Coupon.builder()
        .couponCode(couponCode)
        .couponName(CouponTypeEnum.WELCOME)
        .discountAmount(1000L)
        .issueLimit(1L)
        .issuedAt(issuedAt)
        .expiredAt(expiredAt)
        .minOrderPrice(10000L)
        .build();
  }

  public Coupon toRegularPaymentsCouponEntity(String couponCode, Long discountAmount, Long minOrderPrice, LocalDateTime issuedAt) {

    LocalDateTime expiredAt = issuedAt.plusYears(1);

    return Coupon.builder()
        .couponCode(couponCode)
        .couponName(CouponTypeEnum.YANGBAN)
        .discountAmount(discountAmount)
        .issueLimit(1L)
        .issuedAt(issuedAt)
        .expiredAt(expiredAt)
        .minOrderPrice(minOrderPrice)
        .build();
  }

  public CouponReceipt toCouponReceiptEntity(Coupon coupon, Long consumerId) {

    CouponReceiptId build = CouponReceiptId.builder().coupon(coupon).consumerId(consumerId).build();
    return CouponReceipt.builder().id(build).isUse(false).build();
  }

  public CouponInfoForSingleInquiryResponseDto toInquiryDto(Coupon foundCoupon) {

    return CouponInfoForSingleInquiryResponseDto.builder()
        .couponCode(foundCoupon.getCouponCode())
        .couponName(foundCoupon.getCouponName())
        .discountAmount(foundCoupon.getDiscountAmount())
        .expiredAt(foundCoupon.getExpiredAt())
        .minOrderPrice(foundCoupon.getMinOrderPrice())
        .build();
  }

  public AvailableCouponInfoForSummaryNDetailsResponseDto toSummaryNDetailsDto(
      int availableCount, List<CouponInfoForSingleInquiryResponseDto> availableCouponList) {

    return AvailableCouponInfoForSummaryNDetailsResponseDto.builder()
        .availableCount(availableCount)
        .coupons(availableCouponList)
        .build();
  }

  public CurCouponStatusForReceiveResponseDto toCurCouponStatusDto(
      boolean isSoldOut, boolean isOpen, boolean isDuplicated) {

    return CurCouponStatusForReceiveResponseDto.builder()
        .isSoldOut(isSoldOut)
        .isOpen(isOpen)
        .isDuplicated(isDuplicated)
        .build();
  }

  public SubscriptionCouponBenefitForInquiryResponseDto toSubscriptionCouponBenefitDto(
      long couponUse) {

    return SubscriptionCouponBenefitForInquiryResponseDto.builder().couponUse(couponUse).build();
  }
}
