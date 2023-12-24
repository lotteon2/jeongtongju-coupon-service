package com.jeontongju.coupon.controller;

import com.jeontongju.coupon.dto.response.CurCouponStatusForReceiveResponseDto;
import com.jeontongju.coupon.service.CouponService;
import io.github.bitbox.bitbox.dto.ResponseFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CouponRestController {

  private final CouponService couponService;

  @PostMapping("/consumers/coupons")
  public ResponseEntity<ResponseFormat<CurCouponStatusForReceiveResponseDto>>
      receivePromotionCoupon(@RequestHeader Long memberId) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<CurCouponStatusForReceiveResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("쿠폰 수령 성공")
                .data(couponService.receivePromotionCoupon(memberId))
                .build());
  }
}
