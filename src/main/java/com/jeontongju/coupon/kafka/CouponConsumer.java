package com.jeontongju.coupon.kafka;

import com.jeontongju.coupon.exception.KafkaDuringOrderException;
import com.jeontongju.coupon.service.CouponService;
import com.jeontongju.coupon.utils.CustomErrMessage;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import io.github.bitbox.bitbox.util.KafkaTopicNameInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
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

    try {
      log.info("CouponConsumer's deductCoupon executes..");
      couponService.deductCoupon(orderInfoDto);
    } catch (Exception e) {
      couponProducer.sendRollbackPoint("add-point", orderInfoDto);
      throw new KafkaDuringOrderException(CustomErrMessage.ERROR_KAFKA);
    }
  }

  @KafkaListener(topics = KafkaTopicNameInfo.ROLLBACK_COUPON)
  public void rollbackCouponUsage(OrderInfoDto orderInfoDto) {

    try {
      log.info("CouponConsumer's rollbackCouponUsage executes..");
      couponService.rollbackCouponUsage(orderInfoDto);
      couponProducer.sendRollbackPoint(KafkaTopicNameInfo.CANCEL_ORDER_POINT, orderInfoDto);
    } catch (Exception e) {
      throw new KafkaDuringOrderException(CustomErrMessage.ERROR_KAFKA);
    }
  }
}
