package com.jeontongju.coupon.controller;

import com.jeontongju.coupon.dto.request.OrderPriceForCheckValidRequestDto;
import com.jeontongju.coupon.dto.response.AvailableCouponInfoForSummaryNDetailsResponseDto;
import com.jeontongju.coupon.dto.response.CouponInfoForSingleInquiryResponseDto;
import com.jeontongju.coupon.dto.response.CurCouponStatusForReceiveResponseDto;
import com.jeontongju.coupon.service.CouponService;
import io.github.bitbox.bitbox.dto.ResponseFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

  @GetMapping("/coupons")
  public ResponseEntity<ResponseFormat<Page<CouponInfoForSingleInquiryResponseDto>>>
      getMyCouponsForListLookup(
          @RequestHeader Long memberId,
          @RequestParam(value = "search", required = false) String search,
          @RequestParam("page") int page,
          @RequestParam("size") int size) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Page<CouponInfoForSingleInquiryResponseDto>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("쿠폰 목록 조회 성공")
                .data(couponService.getMyCouponsForListLookup(memberId, page, size, search))
                .build());
  }

  @GetMapping("/consumers/coupons-count")
  public ResponseEntity<ResponseFormat<AvailableCouponInfoForSummaryNDetailsResponseDto>>
      getAvailableCouponsWhenOrdering(
          @RequestHeader Long memberId,
          @Valid @RequestBody OrderPriceForCheckValidRequestDto checkValidRequestDto) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<AvailableCouponInfoForSummaryNDetailsResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("사용 가능한 내 쿠폰 조회 성공")
                .data(couponService.getAvailableCouponsWhenOrdering(memberId, checkValidRequestDto))
                .build());
  }

  @PatchMapping("/coupons/test")
  public ResponseEntity<ResponseFormat<Void>> getCouponTest() {

    couponService.getCouponTest();
    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("[테스트] 쿠폰 수령 성공")
                .build());
  }
}
