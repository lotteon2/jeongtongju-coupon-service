package com.jeontongju.coupon.kafka;

import com.jeontongju.coupon.domain.CouponReceipt;
import com.jeontongju.coupon.exception.KafkaDuringOrderException;
import com.jeontongju.coupon.service.CouponService;
import com.jeontongju.coupon.utils.CustomErrMessage;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import io.github.bitbox.bitbox.util.KafkaTopicNameInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponConsumer {

  private final CouponService couponService;

  @KafkaListener(topics = KafkaTopicNameInfo.REDUCE_STOCK)
  public void deductStock(OrderInfoDto orderInfoDto) {
    try {
      couponService.deductCoupon(orderInfoDto);
    } catch (KafkaException e) {
      throw new KafkaDuringOrderException(CustomErrMessage.ERROR_KAFKA);
    }
  }
}
