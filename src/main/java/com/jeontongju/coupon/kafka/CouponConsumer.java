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

  @KafkaListener(topics = KafkaTopicNameInfo.USE_COUPON)
  public void deductStock(OrderInfoDto orderInfoDto) {

    try {
      log.info("CouponConsumer's deductCoupon executes..");
      couponService.deductCoupon(orderInfoDto);
    } catch (KafkaException e) {
      throw new KafkaDuringOrderException(CustomErrMessage.ERROR_KAFKA);
    }
  }
}
