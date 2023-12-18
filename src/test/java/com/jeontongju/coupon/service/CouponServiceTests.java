package com.jeontongju.coupon.service;

import com.jeontongju.coupon.exception.CouponDiscountAmountOver10PerException;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import io.github.bitbox.bitbox.dto.UserCouponUpdateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CouponServiceTests {

  @Autowired private CouponService couponService;

  @Test
  @DisplayName("쿠폰 할인 금액이 총 주문 금액의 10%를 넘어가면 쿠폰을 사용할 수 없다")
  void t1() {
    UserCouponUpdateDto couponUpdateDto =
        UserCouponUpdateDto.builder()
            .consumerId(1L)
            .couponCode("v5F5-4125-WXHU")
            .couponAmount(12000L)
            .totalAmount(100000L)
            .build();

    OrderInfoDto orderInfoDto = OrderInfoDto.builder().userCouponUpdateDto(couponUpdateDto).build();


    Assertions.assertThrows(CouponDiscountAmountOver10PerException.class, () -> {
      couponService.deductCoupon(orderInfoDto);
    });
  }
}
