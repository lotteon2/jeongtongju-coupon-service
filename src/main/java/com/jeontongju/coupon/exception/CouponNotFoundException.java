package com.jeontongju.coupon.exception;

public class CouponNotFoundException extends RuntimeException {

  public CouponNotFoundException(String msg) {
    super(msg);
  }
}
