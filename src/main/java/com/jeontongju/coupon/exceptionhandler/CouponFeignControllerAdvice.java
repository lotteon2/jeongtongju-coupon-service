package com.jeontongju.coupon.exceptionhandler;

import com.jeontongju.coupon.exception.*;
import io.github.bitbox.bitbox.dto.FeignFormat;
import io.github.bitbox.bitbox.dto.ResponseFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class CouponFeignControllerAdvice {

  @ExceptionHandler(CouponNotFoundException.class)
  public FeignFormat<Void> handleNotFoundCoupon() {

    return FeignFormat.<Void>builder()
        .code(HttpStatus.OK.value())
        .failure("NOT_FOUND_COUPON")
        .build();
  }

  @ExceptionHandler(InsufficientMinOrderPriceException.class)
  public FeignFormat<Void> handleCouponPolicyException() {

    return FeignFormat.<Void>builder()
        .code(HttpStatus.OK.value())
        .failure("INSUFFICIENT_MIN_ORDER_PRICE")
        .build();
  }

  @ExceptionHandler(CouponExpiredException.class)
  public FeignFormat<Void> handleExpiredCoupon() {

    return FeignFormat.<Void>builder()
        .code(HttpStatus.OK.value())
        .failure("EXPIRED_COUPON")
        .build();
  }

  @ExceptionHandler(IncorrectCouponDiscountAmountException.class)
  public FeignFormat<Void> handleIncorrectCouponDiscountAmount() {

    return FeignFormat.<Void>builder()
        .code(HttpStatus.OK.value())
        .failure("INCORRECT_COUPON_DISCOUNT_AMOUNT")
        .build();
  }

  @ExceptionHandler(KafkaDuringOrderException.class)
  public ResponseEntity<ResponseFormat<Void>> handleKafkaDuringOrderException() {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(
            ResponseFormat.<Void>builder()
                .code(status.value())
                .message(status.name())
                .failure("KAFKA_ERROR")
                .build());
  }
}
