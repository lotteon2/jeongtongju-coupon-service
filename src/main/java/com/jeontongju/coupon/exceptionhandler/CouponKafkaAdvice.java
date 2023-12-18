package com.jeontongju.coupon.exceptionhandler;

import com.jeontongju.coupon.exception.*;
import io.github.bitbox.bitbox.dto.ResponseFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class CouponKafkaAdvice {

  @ExceptionHandler(CouponNotFoundException.class)
  public ResponseEntity<ResponseFormat<Void>> handleNotFoundCoupon() {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(
            ResponseFormat.<Void>builder()
                .code(status.value())
                .message(status.name())
                .failure("NOT_FOUND_COUPON")
                .build());
  }

  @ExceptionHandler(InsufficientMinOrderPriceException.class)
  public ResponseEntity<ResponseFormat<Void>> handleCouponPolicyException() {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(
            ResponseFormat.<Void>builder()
                .code(status.value())
                .message(status.name())
                .failure("INSUFFICIENT_MIN_ORDER_PRICE")
                .build());
  }

  @ExceptionHandler(CouponExpiredException.class)
  public ResponseEntity<ResponseFormat<Void>> handleExpiredCoupon() {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(
            ResponseFormat.<Void>builder()
                .code(status.value())
                .message(status.name())
                .failure("EXPIRED")
                .build());
  }

  @ExceptionHandler(IncorrectCouponDiscountAmountException.class)
  public ResponseEntity<ResponseFormat<Void>> handleIncorrectCouponDiscountAmount() {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(
            ResponseFormat.<Void>builder()
                .code(status.value())
                .message(status.name())
                .failure("INCORRECT_COUPON_DISCOUNT_AMOUNT")
                .build());
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
