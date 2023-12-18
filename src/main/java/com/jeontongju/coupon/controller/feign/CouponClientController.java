package com.jeontongju.coupon.controller.feign;

import com.jeontongju.coupon.service.CouponService;
import io.github.bitbox.bitbox.dto.FeignFormat;
import io.github.bitbox.bitbox.dto.UserCouponUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponClientController {

  private final CouponService couponService;

  @GetMapping("/coupons")
  FeignFormat<Void> checkCouponInfo(@RequestBody UserCouponUpdateDto userCouponUpdateDto) {

    couponService.checkCouponInfo(userCouponUpdateDto);
    return FeignFormat.<Void>builder().code(HttpStatus.OK.value()).build();
  }
}
