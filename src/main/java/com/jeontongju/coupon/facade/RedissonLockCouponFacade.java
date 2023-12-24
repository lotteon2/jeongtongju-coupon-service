package com.jeontongju.coupon.facade;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.exception.CouponExhaustedException;
import com.jeontongju.coupon.exception.CouponNotFoundException;
import com.jeontongju.coupon.repository.CouponRepository;
import com.jeontongju.coupon.utils.CustomErrMessage;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockCouponFacade {

  private final RedissonClient redissonClient;
  private final CouponRepository couponRepository;

  public void decrease(String id, Long quantity) {

    RLock lock = redissonClient.getLock(id);
    try {
      boolean available = lock.tryLock(15, 1, TimeUnit.SECONDS);

      if (!available) {
        log.info("lock 획득 실패");
        return;
      }
      decreasePromotionCoupon(id, quantity);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  /**
   * 쿠폰 1개씩 차감
   *
   * @param couponCode
   * @param quantity
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void decreasePromotionCoupon(String couponCode, Long quantity)
      throws CouponExhaustedException {

    Coupon foundCoupon =
        couponRepository
            .findByCouponCode(couponCode)
            .orElseThrow(() -> new CouponNotFoundException(CustomErrMessage.NOT_FOUND_COUPON));

    foundCoupon.decrease(quantity);

    couponRepository.saveAndFlush(foundCoupon);
  }
}
