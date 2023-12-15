package com.jeontongju.coupon.service;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.domain.CouponReceipt;
import com.jeontongju.coupon.exception.CouponDiscountAmountOver10PerException;
import com.jeontongju.coupon.exception.CouponNotFoundException;
import com.jeontongju.coupon.exception.KafkaDuringOrderException;
import com.jeontongju.coupon.kafka.CouponProducer;
import com.jeontongju.coupon.repository.CouponReceiptRepository;
import com.jeontongju.coupon.repository.CouponRepository;
import com.jeontongju.coupon.utils.CustomErrMessage;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import io.github.bitbox.bitbox.dto.UserCouponUpdateDto;
import io.github.bitbox.bitbox.util.KafkaTopicNameInfo;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;
  private final CouponReceiptRepository couponReceiptRepository;

  private final CouponProducer couponProducer;

  @Transactional
  public void deductCoupon(OrderInfoDto orderInfoDto) {

    UserCouponUpdateDto userCouponUpdateDto = orderInfoDto.getUserCouponUpdateDto();

    Long couponAmount = userCouponUpdateDto.getCouponAmount();
    Long totalAmount = userCouponUpdateDto.getTotalAmount();
    if (couponAmount >= totalAmount * 0.1)
      throw new CouponDiscountAmountOver10PerException(
          CustomErrMessage.COUPON_DISCOUNT_AMOUNT_OVER_10_PER);

    Coupon foundCoupon =
        couponRepository
            .findByCouponCode(userCouponUpdateDto.getCouponCode())
            .orElseThrow(() -> new CouponNotFoundException(CustomErrMessage.NOT_FOUND_COUPON));

    CouponReceipt foundCouponReceipt =
        couponReceiptRepository
            .findByCouponReceiptId(userCouponUpdateDto.getConsumerId(), foundCoupon)
            .orElseThrow();

    foundCouponReceipt.deductCoupon();

    try {
      couponProducer.sendUpdateStock(KafkaTopicNameInfo.REDUCE_STOCK, orderInfoDto);
    } catch (KafkaException e) {
      rollbackOrderInfo(foundCouponReceipt, orderInfoDto);
      throw new KafkaDuringOrderException(CustomErrMessage.ERROR_KAFKA);
    }
  }

  private void rollbackOrderInfo(CouponReceipt couponReceipt, OrderInfoDto orderInfoDto) {

    couponReceipt.rollbackCoupon();

    try {
      couponProducer.sendRollbackPoint(
          KafkaTopicNameInfo.CANCEL_ORDER_POINT, orderInfoDto.getUserPointUpdateDto());
    } catch (KafkaException e) {
      throw new KafkaDuringOrderException(CustomErrMessage.ROLLBACK_ERROR_KAFKA);
    }
  }
}
