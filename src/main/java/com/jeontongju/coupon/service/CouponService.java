package com.jeontongju.coupon.service;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.domain.CouponReceipt;
import com.jeontongju.coupon.exception.CouponDiscountAmountOver10PerException;
import com.jeontongju.coupon.exception.CouponExpiredException;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;
  private final CouponReceiptRepository couponReceiptRepository;

  private final CouponProducer couponProducer;

  @Transactional
  public void deductCoupon(OrderInfoDto orderInfoDto) {

    log.info("CouponService's deductCoupon executes..");

    UserCouponUpdateDto userCouponUpdateDto = orderInfoDto.getUserCouponUpdateDto();

    Long couponAmount = userCouponUpdateDto.getCouponAmount();
    Long totalAmount = userCouponUpdateDto.getTotalAmount();
    if (couponAmount > totalAmount * 0.1)
      throw new CouponDiscountAmountOver10PerException(
          CustomErrMessage.COUPON_DISCOUNT_AMOUNT_OVER_10_PER);

    Coupon foundCoupon =
        couponRepository
            .findByCouponCode(userCouponUpdateDto.getCouponCode())
            .orElseThrow(() -> new CouponNotFoundException(CustomErrMessage.NOT_FOUND_COUPON));

    CouponReceipt foundCouponReceipt =
        couponReceiptRepository
            .findByCouponReceiptId(userCouponUpdateDto.getConsumerId(), foundCoupon)
            .orElseThrow(() -> new CouponNotFoundException(CustomErrMessage.NOT_FOUND_COUPON));

    foundCouponReceipt.deductCoupon();

    log.info("CouponService's deductCoupon Successful executed!");
    couponProducer.sendUpdateStock(KafkaTopicNameInfo.REDUCE_STOCK, orderInfoDto);
  }

  public void rollbackOrderInfo(CouponReceipt couponReceipt, OrderInfoDto orderInfoDto) {

    couponReceipt.rollbackCoupon();

    try {
      couponProducer.sendRollbackPoint(
          KafkaTopicNameInfo.CANCEL_ORDER_POINT, orderInfoDto.getUserPointUpdateDto());
    } catch (KafkaException e) {
      throw new KafkaDuringOrderException(CustomErrMessage.ROLLBACK_ERROR_KAFKA);
    }
  }

  public void checkCouponInfo(UserCouponUpdateDto userCouponUpdateDto) {

    Coupon foundCoupon =
        couponRepository
            .findByCouponCode(userCouponUpdateDto.getCouponCode())
            .orElseThrow(() -> new CouponNotFoundException(CustomErrMessage.NOT_FOUND_COUPON));

    // 쿠폰 만료 여부 확인
    Boolean isValid = checkValidCoupon(foundCoupon.getExpiredAt());
    if (!isValid) throw new CouponExpiredException(CustomErrMessage.EXPIRED_COUPON);

    // 쿠폰 할인 정책 확인
    Long couponAmount = userCouponUpdateDto.getCouponAmount();
    Long totalAmount = userCouponUpdateDto.getTotalAmount();
    if (couponAmount > totalAmount * 0.1)
      throw new CouponDiscountAmountOver10PerException(
          CustomErrMessage.COUPON_DISCOUNT_AMOUNT_OVER_10_PER);
  }

  private Boolean checkValidCoupon(Timestamp expiredAt) {

    long currentTimeMillis = System.currentTimeMillis();
    Timestamp currentTimestamp = new Timestamp(currentTimeMillis);

    int comparisonResult = currentTimestamp.compareTo(expiredAt);
    return comparisonResult <= 0;
  }
}
