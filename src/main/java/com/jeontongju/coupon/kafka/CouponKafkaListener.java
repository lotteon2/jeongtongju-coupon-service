package com.jeontongju.coupon.kafka;

import com.jeontongju.coupon.service.CouponService;
import io.github.bitbox.bitbox.dto.*;
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
public class CouponKafkaListener {

  private final CouponService couponService;
  private final CouponKafkaProducer couponKafkaProducer;

  /**
   * 주문 시, 주문 및 결제 확정을 위한 쿠폰 사용 처리
   *
   * @param orderInfoDto 주문 정보
   */
  @KafkaListener(topics = KafkaTopicNameInfo.USE_COUPON)
  public void deductCoupon(OrderInfoDto orderInfoDto) {

    try {
      couponService.deductCoupon(orderInfoDto);
      // 상품 서버로 재고 차감 요청
      couponKafkaProducer.send(KafkaTopicNameInfo.REDUCE_STOCK, orderInfoDto);
    } catch (Exception e) {
      log.error("During Order Process: Error while deduct coupon={}", e.getMessage());
      // 회원 서버로 포인트 환불 요청
      couponKafkaProducer.send(KafkaTopicNameInfo.ADD_POINT, orderInfoDto);
      couponKafkaProducer.send(
          KafkaTopicNameInfo.SEND_ERROR_NOTIFICATION,
          ServerErrorForNotificationDto.builder()
              .recipientId(orderInfoDto.getUserCouponUpdateDto().getConsumerId())
              .recipientType(RecipientTypeEnum.ROLE_CONSUMER)
              .notificationType(NotificationTypeEnum.INTERNAL_COUPON_SERVER_ERROR)
              .error(orderInfoDto)
              .build());
    }
  }

  /**
   * 주문 실패 시, 쿠폰 미사용 상태로 처리(복구)
   *
   * @param orderInfoDto 주문 복구 정보
   */
  @KafkaListener(topics = KafkaTopicNameInfo.ROLLBACK_COUPON)
  public void rollbackCouponUsage(OrderInfoDto orderInfoDto) {

    try {

      couponService.rollbackCouponUsage(orderInfoDto);

      if (orderInfoDto.getUserPointUpdateDto().getPoint() > 0) {
        // 회원 서버로 포인트 환불 요청
        couponKafkaProducer.send(KafkaTopicNameInfo.ADD_POINT, orderInfoDto);
      }
    } catch (Exception e) {
      log.error("During Order-Rollback Process: Error while rollback coupon={}", e.getMessage());
    }
  }

  /**
   * 주문 취소 시, 해당 쿠폰 미사용 처리(환불)
   *
   * @param orderCancelDto 주문 취소 정보
   */
  @KafkaListener(topics = KafkaTopicNameInfo.CANCEL_ORDER_COUPON)
  public void refundCouponByOrderCancel(OrderCancelDto orderCancelDto) {

    try {
      couponService.refundCouponByOrderCancel(orderCancelDto);
      // 재고 서버로 결제 취소 요청
      couponKafkaProducer.send(KafkaTopicNameInfo.CANCEL_ORDER_PAYMENT, orderCancelDto);
    } catch (Exception e) {
      log.error("During Order Cancel Process: Error while refund coupon={}", e.getMessage());
    }
  }

  /**
   * 주문 취소 실패 시, 쿠폰 사용 상태로 처리(복구)
   *
   * @param orderCancelDto 주문 복구 정보
   */
  @KafkaListener(topics = KafkaTopicNameInfo.RECOVER_CANCEL_ORDER_COUPON)
  public void recoverCouponByFailedOrderCancel(OrderCancelDto orderCancelDto) {

    try {
      couponService.recoverCouponByFailedOrderCancel(orderCancelDto);

      if (orderCancelDto.getPoint() <= 0) {
        couponKafkaProducer.send(KafkaTopicNameInfo.RECOVER_CANCEL_ORDER, orderCancelDto);
      } else {
        couponKafkaProducer.send(KafkaTopicNameInfo.RECOVER_CANCEL_ORDER_POINT, orderCancelDto);
      }
    } catch (Exception e) {
      log.error(
          "During Recover Order By Order Cancel Fail: Error while recovering coupon={}",
          e.getMessage());
      couponKafkaProducer.send(
          KafkaTopicNameInfo.SEND_ERROR_CANCELING_ORDER_NOTIFICATION,
          MemberInfoForNotificationDto.builder()
              .recipientId(orderCancelDto.getConsumerId())
              .recipientType(RecipientTypeEnum.ROLE_CONSUMER)
              .notificationType(NotificationTypeEnum.INTERNAL_COUPON_SERVER_ERROR)
              .build());
    }
  }

  /**
   * 구독 결제 완료 후, 해당 소비자 구독 전용 쿠폰 자동 수령 처리
   *
   * @param regularPaymentsCouponDto 구독 결제 정보(소비자, 결제 완료 시각)
   */
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
