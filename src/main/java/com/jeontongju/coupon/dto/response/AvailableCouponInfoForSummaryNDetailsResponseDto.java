package com.jeontongju.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AvailableCouponInfoForSummaryNDetailsResponseDto {

  private int totalCount;
  private int availableCount;
  private List<CouponInfoForSingleInquiryResponseDto> coupons;
}
