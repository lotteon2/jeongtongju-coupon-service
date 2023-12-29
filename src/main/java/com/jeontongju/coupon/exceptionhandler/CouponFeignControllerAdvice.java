package com.jeontongju.coupon.exceptionhandler;

import com.jeontongju.coupon.dto.response.CurCouponStatusForReceiveResponseDto;
import com.jeontongju.coupon.exception.*;
import com.jeontongju.coupon.mapper.CouponMapper;
import io.github.bitbox.bitbox.dto.FeignFormat;
import io.github.bitbox.bitbox.dto.ResponseFormat;
import io.github.bitbox.bitbox.enums.FailureTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class CouponFeignControllerAdvice {

  private final CouponMapper couponMapper;

  @ExceptionHandler(CouponNotFoundException.class)
  public FeignFormat<Void> handleNotFoundCoupon() {

    return FeignFormat.<Void>builder()
        .code(HttpStatus.OK.value())
        .failure(FailureTypeEnum.NOT_FOUND_COUPON)
        .build();
  }

  @ExceptionHandler(InsufficientMinOrderPriceException.class)
  public FeignFormat<Void> handleCouponPolicyException() {

    return FeignFormat.<Void>builder()
        .code(HttpStatus.OK.value())
        .failure(FailureTypeEnum.INSUFFICIENT_MIN_ORDER_PRICE)
        .build();
  }

  @ExceptionHandler(AlreadyUseCouponException.class)
  public FeignFormat<Void> handleAlreadyUseCoupon() {

    // TODO
    return FeignFormat.<Void>builder()
        .code(HttpStatus.OK.value())
        .failure(FailureTypeEnum.NOT_FOUND_COUPON)
        .build();
  }

  @ExceptionHandler(CouponExpiredException.class)
  public FeignFormat<Void> handleExpiredCoupon() {

    return FeignFormat.<Void>builder()
        .code(HttpStatus.OK.value())
        .failure(FailureTypeEnum.EXPIRED_COUPON)
        .build();
  }

  @ExceptionHandler(IncorrectCouponDiscountAmountException.class)
  public FeignFormat<Void> handleIncorrectCouponDiscountAmount() {

    return FeignFormat.<Void>builder()
        .code(HttpStatus.OK.value())
        .failure(FailureTypeEnum.INCORRECT_COUPON_DISCOUNT_AMOUNT)
        .build();
  }

  @ExceptionHandler(NotOpenPromotionCouponEventException.class)
  public FeignFormat<CurCouponStatusForReceiveResponseDto> handleNotOpenPromotionCouponEvent() {

    return FeignFormat.<CurCouponStatusForReceiveResponseDto>builder()
        .code(HttpStatus.BAD_REQUEST.value())
        .data(couponMapper.toCurCouponStatusDto(false, false))
        .build();
  }

  @ExceptionHandler(CouponExhaustedException.class)
  public FeignFormat<CurCouponStatusForReceiveResponseDto> handleCouponExhausted() {

    return FeignFormat.<CurCouponStatusForReceiveResponseDto>builder()
        .code(HttpStatus.BAD_REQUEST.value())
        .data(couponMapper.toCurCouponStatusDto(true, true))
        .build();
  }

  @ExceptionHandler(KafkaDuringOrderException.class)
  public ResponseEntity<ResponseFormat<Void>> handleKafkaDuringOrderException() {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(ResponseFormat.<Void>builder().code(status.value()).message(status.name()).build());
  }
}
