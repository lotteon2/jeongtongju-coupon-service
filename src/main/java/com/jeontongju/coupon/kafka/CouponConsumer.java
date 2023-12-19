package com.jeontongju.coupon.kafka;

import com.jeontongju.coupon.service.CouponService;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import io.github.bitbox.bitbox.util.KafkaTopicNameInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponConsumer {

  private final CouponService couponService;
  private final CouponProducer couponProducer;

  @KafkaListener(topics = KafkaTopicNameInfo.USE_COUPON)
  public void deductCoupon(OrderInfoDto orderInfoDto) {

    log.info("KafkaListener's topic={}", KafkaTopicNameInfo.USE_COUPON);
    try {
      couponService.deductCoupon(orderInfoDto);
    } catch (Exception e) {
      log.error("deduct coupon exception={}", e.getMessage());
      e.printStackTrace();
      couponProducer.sendRollbackPoint("add-point", orderInfoDto);
    }
  }

  @KafkaListener(topics = KafkaTopicNameInfo.ROLLBACK_COUPON)
  public void rollbackCouponUsage(OrderInfoDto orderInfoDto) {

    try {
      couponService.rollbackCouponUsage(orderInfoDto);
      couponProducer.sendRollbackPoint("add-point", orderInfoDto);
    } catch (Exception e) {
      log.error("rollback point exception={}", e.getMessage());
    }
  }
}
