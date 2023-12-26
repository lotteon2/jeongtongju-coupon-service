package com.jeontongju.coupon.mapper;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.domain.CouponReceipt;
import com.jeontongju.coupon.domain.CouponReceiptId;
import com.jeontongju.coupon.dto.response.AvailableCouponInfoForSummaryNDetailsResponseDto;
import com.jeontongju.coupon.dto.response.CouponInfoForSingleInquiryResponseDto;
import com.jeontongju.coupon.dto.response.CurCouponStatusForReceiveResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponMapper {

  public CouponReceipt toCouponReceiptEntity(Coupon coupon, Long consumerId) {

    CouponReceiptId build = CouponReceiptId.builder().coupon(coupon).consumerId(consumerId).build();
    return CouponReceipt.builder().id(build).isUse(false).build();
  }

  public CurCouponStatusForReceiveResponseDto toCurCouponStatusDto() {

    return CurCouponStatusForReceiveResponseDto.builder().isSoldOut(false).isOpen(true).build();
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
      int totalValidCounts,
      int availableCount,
      List<CouponInfoForSingleInquiryResponseDto> availableCouponList) {

    return AvailableCouponInfoForSummaryNDetailsResponseDto.builder()
        .totalCount(totalValidCounts)
        .availableCount(availableCount)
        .coupons(availableCouponList)
        .build();
  }
}
