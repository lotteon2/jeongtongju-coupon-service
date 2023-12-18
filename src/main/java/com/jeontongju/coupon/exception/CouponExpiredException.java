package com.jeontongju.coupon.exception;

public class CouponExpiredException extends RuntimeException {

  public CouponExpiredException(String msg) {
    super(msg);
  }
}
