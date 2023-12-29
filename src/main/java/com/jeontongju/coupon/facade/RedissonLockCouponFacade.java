package com.jeontongju.coupon.facade;

import com.jeontongju.coupon.utils.ReceiveManager;
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
  private final ReceiveManager receiveManager;

  public void decrease(String id, Long quantity) {

    RLock lock = redissonClient.getLock(id);
    try {
      boolean available = lock.tryLock(40, 1, TimeUnit.SECONDS);

      if (!available) {
        log.info("lock 획득 실패");
        return;
      }
      receiveManager.decreasePromotionCoupon(id, quantity);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }
}
