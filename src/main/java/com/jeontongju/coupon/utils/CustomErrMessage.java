package com.jeontongju.coupon.utils;

public interface CustomErrMessage {

  String ERROR_KAFKA = "카프카 예외 발생";
  String NOT_FOUND_COUPON = "존재하지 않는 쿠폰";
  String NOT_FOUND_COUPON_RECEIPT = "존재 하지 않는 쿠폰 수령 내역";
  String EXPIRED_COUPON = "만료된 쿠폰";
  String INSUFFICIENT_MIN_ORDER_PRICE = "최소 주문 금액 미달";
  String INCORRECT_COUPON_DISCOUNT_AMOUNT = "쿠폰 코드와 할인 금액 불일치";
  String ALREADY_USE_COUPON = "이미 사용한 쿠폰";
  String ALREADY_RECEIVE_COUPON = "이미 수령한 쿠폰";
  String EXHAUSTED_COUPON = "쿠폰 소진";
  String NOT_OPEN_PROMOTION_COUPON_EVENT = "프로모션 쿠폰 이벤트 시각 미오픈";
}
