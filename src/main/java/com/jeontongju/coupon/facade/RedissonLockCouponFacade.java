package com.jeontongju.coupon.facade;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.service.CouponService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockCouponFacade {

  private final RedissonClient redissonClient;
  private final CouponService couponService;
  public void decrease(Long quantity, Long consumerId) {

    Coupon promotionCoupon = couponService.getPromotionCoupon();
    String id = promotionCoupon.getCouponCode();
    RLock lock = redissonClient.getLock(id);
    try {
      boolean available = lock.tryLock(15, 1, TimeUnit.SECONDS);

      if (!available) {
        log.info("lock 획득 실패");
        return;
      }

      Coupon foundCoupon = couponService.getCoupon(id);
      couponService.decreasePromotionCoupon(foundCoupon, quantity, consumerId);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      log.info("[finally executes]");
      if (lock.isLocked() && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }
}
