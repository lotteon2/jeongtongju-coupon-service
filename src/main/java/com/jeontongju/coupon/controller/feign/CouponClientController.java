package com.jeontongju.coupon.controller.feign;

import com.jeontongju.coupon.service.CouponService;
import io.github.bitbox.bitbox.dto.FeignFormat;
import io.github.bitbox.bitbox.dto.SubscriptionCouponBenefitForInquiryResponseDto;
import io.github.bitbox.bitbox.dto.UserCouponUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CouponClientController {

  private final CouponService couponService;

  @PostMapping("/coupons")
  FeignFormat<Void> checkCouponInfo(@RequestBody UserCouponUpdateDto userCouponUpdateDto) {

    couponService.checkCouponInfo(userCouponUpdateDto);
    return FeignFormat.<Void>builder().code(HttpStatus.OK.value()).build();
  }

  @GetMapping("/consumers/{consumerId}/coupons/benefit")
  FeignFormat<SubscriptionCouponBenefitForInquiryResponseDto> getSubscriptionBenefit(
      @PathVariable Long consumerId) {

    return FeignFormat.<SubscriptionCouponBenefitForInquiryResponseDto>builder()
        .code(HttpStatus.OK.value())
        .data(couponService.getSubscriptionBenefit(consumerId))
        .build();
  }

  @GetMapping("/consumers/{consumerId}/promotion-coupon/prev-check")
  FeignFormat<Boolean> prevCheck(@PathVariable Long consumerId) {

    return FeignFormat.<Boolean>builder()
            .code(HttpStatus.OK.value())
            .data(couponService.prevCheck(consumerId))
            .build();
  }
}
