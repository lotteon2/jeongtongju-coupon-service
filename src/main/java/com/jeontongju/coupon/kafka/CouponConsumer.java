package com.jeontongju.coupon.kafka;

import com.jeontongju.coupon.service.CouponService;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponConsumer {

  private final CouponService couponService;

  private final String REDUCE_STOCK = "reduce-stock";

  @KafkaListener(topics = REDUCE_STOCK)
  public void deductStock(OrderInfoDto orderInfoDto) {
    couponService.deductCoupon(orderInfoDto);
  }
}
