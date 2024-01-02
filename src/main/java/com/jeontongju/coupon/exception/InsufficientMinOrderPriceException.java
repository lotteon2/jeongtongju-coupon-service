package com.jeontongju.coupon.exception;

public class InsufficientMinOrderPriceException extends RuntimeException {

  public InsufficientMinOrderPriceException(String msg) {
    super(msg);
  }
}
