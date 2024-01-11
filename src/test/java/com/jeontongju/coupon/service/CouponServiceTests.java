package com.jeontongju.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.facade.RedissonLockCouponFacade;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CouponServiceTests {

  @Autowired private CouponService couponService;
  @Autowired private RedissonLockCouponFacade redissonLockCouponFacade;

  final String PROMOTION_COUPON_CODE = "v5F5-4125-WXHz";

  @Test
  @DisplayName("프로모션 쿠폰 수령 시, 동시성 문제를 해결할 수 있다")
  public void 동시_100개_요청() throws InterruptedException {

    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);

    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executorService.submit(
          () -> {
            try {
              redissonLockCouponFacade.decrease(1L);
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await();

    Coupon coupon = couponService.getCoupon(PROMOTION_COUPON_CODE);
    assertThat(coupon.getIssueLimit()).isEqualTo(0L);
  }
}
