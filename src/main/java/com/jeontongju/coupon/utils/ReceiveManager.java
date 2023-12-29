package com.jeontongju.coupon.utils;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.exception.CouponNotFoundException;
import com.jeontongju.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReceiveManager {

  private final CouponRepository couponRepository;

  /**
   * 쿠폰 1개씩 차감
   *
   * @param couponCode
   * @param quantity
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void decreasePromotionCoupon(String couponCode, Long quantity) {

    Coupon foundCoupon =
        couponRepository
            .findByCouponCode(couponCode)
            .orElseThrow(() -> new CouponNotFoundException(CustomErrMessage.NOT_FOUND_COUPON));

    foundCoupon.decrease(quantity);

    couponRepository.saveAndFlush(foundCoupon);
  }
}
