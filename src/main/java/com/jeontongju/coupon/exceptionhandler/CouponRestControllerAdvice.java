package com.jeontongju.coupon.exceptionhandler;

import com.jeontongju.coupon.dto.response.CurCouponStatusForReceiveResponseDto;
import com.jeontongju.coupon.exception.*;
import com.jeontongju.coupon.mapper.CouponMapper;
import io.github.bitbox.bitbox.dto.ResponseFormat;
import io.github.bitbox.bitbox.enums.FailureTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class CouponRestControllerAdvice {

  private final CouponMapper couponMapper;

  @ExceptionHandler(CouponNotFoundException.class)
  public ResponseEntity<ResponseFormat<Void>> handleNotFoundCoupon() {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Void>builder()
                .code(HttpStatus.OK.value())
                .failure(FailureTypeEnum.NOT_FOUND_COUPON)
                .build());
  }

  @ExceptionHandler(KafkaDuringOrderException.class)
  public ResponseEntity<ResponseFormat<Void>> handleKafkaDuringOrderException() {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(ResponseFormat.<Void>builder().code(status.value()).build());
  }

  @ExceptionHandler(NotOpenPromotionCouponEventException.class)
  public ResponseEntity<ResponseFormat<CurCouponStatusForReceiveResponseDto>>
      handleNotOpenPromotionCouponEvent() {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<CurCouponStatusForReceiveResponseDto>builder()
                .code(HttpStatus.OK.value())
                .data(couponMapper.toCurCouponStatusDto(false, false, false))
                .build());
  }

  @ExceptionHandler(CouponExhaustedException.class)
  public ResponseEntity<ResponseFormat<CurCouponStatusForReceiveResponseDto>>
      handleCouponExhausted() {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<CurCouponStatusForReceiveResponseDto>builder()
                .code(HttpStatus.OK.value())
                .data(couponMapper.toCurCouponStatusDto(true, true, false))
                .build());
  }

  @ExceptionHandler(AlreadyReceivePromotionCouponException.class)
  public ResponseEntity<ResponseFormat<CurCouponStatusForReceiveResponseDto>>
      handleAlreadyReceivePromotionCoupon() {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<CurCouponStatusForReceiveResponseDto>builder()
                .code(HttpStatus.OK.value())
                .data(couponMapper.toCurCouponStatusDto(false, true, true))
                .build());
  }
}
