package com.jeontongju.coupon.exception;

public class AlreadyUseCouponException extends RuntimeException {

  public AlreadyUseCouponException(String msg) {
    super(msg);
  }
}
