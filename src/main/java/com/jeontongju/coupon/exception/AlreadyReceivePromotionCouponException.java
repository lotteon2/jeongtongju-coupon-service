package com.jeontongju.coupon.exception;

public class AlreadyReceivePromotionCouponException extends RuntimeException {

  public AlreadyReceivePromotionCouponException(String msg) {
    super(msg);
  }
}
