package com.jeontongju.coupon.kafka;

import com.jeontongju.coupon.service.CouponService;
import io.github.bitbox.bitbox.dto.ConsumerRegularPaymentsCouponDto;
import io.github.bitbox.bitbox.dto.OrderCancelDto;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import io.github.bitbox.bitbox.dto.ServerErrorForNotificationDto;
import io.github.bitbox.bitbox.enums.NotificationTypeEnum;
import io.github.bitbox.bitbox.enums.RecipientTypeEnum;
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

    try {
      couponService.deductCoupon(orderInfoDto);
      // 상품 서버로 재고 차감 요청
      couponProducer.send(KafkaTopicNameInfo.REDUCE_STOCK, orderInfoDto);
    } catch (Exception e) {
      log.error("During Order Process: Error while deduct coupon={}", e.getMessage());
      // 회원 서버로 포인트 환불 요청
      couponProducer.send(KafkaTopicNameInfo.ADD_POINT, orderInfoDto);
      couponProducer.send(
          KafkaTopicNameInfo.SEND_ERROR_NOTIFICATION,
          ServerErrorForNotificationDto.builder()
              .recipientId(orderInfoDto.getUserCouponUpdateDto().getConsumerId())
              .recipientType(RecipientTypeEnum.ROLE_CONSUMER)
              .notificationType(NotificationTypeEnum.INTERNAL_COUPON_SERVER_ERROR)
              .error(orderInfoDto)
              .build());
    }
  }

  @KafkaListener(topics = KafkaTopicNameInfo.ROLLBACK_COUPON)
  public void rollbackCouponUsage(OrderInfoDto orderInfoDto) {

    try {
      couponService.rollbackCouponUsage(orderInfoDto);
      // 회원 서버로 포인트 환불 요청
      couponProducer.send(KafkaTopicNameInfo.ADD_POINT, orderInfoDto);
    } catch (Exception e) {
      log.error("During Order-Rollback Process: Error while rollback coupon={}", e.getMessage());
    }
  }

  @KafkaListener(topics = KafkaTopicNameInfo.CANCEL_ORDER_COUPON)
  public void refundCouponByOrderCancel(OrderCancelDto orderCancelDto) {

    try {
      couponService.refundCouponByOrderCancel(orderCancelDto);
      // 결제 서버로 결제 취소 요청
      couponProducer.send(KafkaTopicNameInfo.CANCEL_ORDER_PAYMENT, orderCancelDto);
    } catch (Exception e) {
      log.error("During Order Cancel Process: Error while refund coupon={}", e.getMessage());
    }
  }

  @KafkaListener(topics = KafkaTopicNameInfo.ISSUE_REGULAR_PAYMENTS_COUPON)
  public void giveRegularPaymentsCoupon(ConsumerRegularPaymentsCouponDto regularPaymentsCouponDto) {

    try {
      couponService.giveRegularPaymentsCoupon(regularPaymentsCouponDto);
    } catch (Exception e) {
      log.error(
          "After Successful Subscription-Payments: Error while give Coupon={}", e.getMessage());
    }
  }
}
