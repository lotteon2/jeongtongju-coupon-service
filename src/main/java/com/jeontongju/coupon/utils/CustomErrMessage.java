package com.jeontongju.coupon.utils;

public interface CustomErrMessage {

  String ERROR_KAFKA = "카프카 예외 발생";
  String ROLLBACK_ERROR_KAFKA = "카프카 롤백 중, 예외 발생";
  String COUPON_DISCOUNT_AMOUNT_OVER_10_PER = "쿠폰 할인 금액 10% 초과";
  String NOT_FOUND_COUPON = "존재하지 않는 쿠폰";
  String EXPIRED_COUPON = "만료된 쿠폰";
}
