package com.jeontongju.coupon.exceptionhandler;

import com.jeontongju.coupon.exception.CouponDiscountAmountOver10PerException;
import com.jeontongju.coupon.utils.CustomErrMessage;
import io.github.bitbox.bitbox.dto.FeignFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class CouponKafkaAdvice {

  @ExceptionHandler(CouponDiscountAmountOver10PerException.class)
  public FeignFormat<Void> handleCouponPolicyException() {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    return FeignFormat.<Void>builder().code(status.value()).failure("EXPIRED").build();
  }
}
