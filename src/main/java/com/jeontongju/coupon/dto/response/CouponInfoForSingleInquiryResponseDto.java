package com.jeontongju.coupon.dto.response;

import io.github.bitbox.bitbox.enums.CouponTypeEnum;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CouponInfoForSingleInquiryResponseDto {

  private String couponCode;
  private CouponTypeEnum couponName;
  private Long discountAmount;
  private LocalDateTime expiredAt;
  private Long minOrderPrice;
}
