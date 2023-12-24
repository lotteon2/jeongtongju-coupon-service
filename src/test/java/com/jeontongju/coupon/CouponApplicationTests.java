package com.jeontongju.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.facade.RedissonLockCouponFacade;
import com.jeontongju.coupon.service.CouponService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CouponApplicationTests {

  @Autowired private CouponService couponService;

  @Autowired private RedissonLockCouponFacade redissonLockCouponFacade;

  @Test
  @DisplayName("프로모션 쿠폰 수령 시, 동시에 요청이 들어오면 Race Condition 문제가 생긴다.")
  public void 동시_100개_요청() throws InterruptedException {

    int threadCount = 100;

    ExecutorService executorService = Executors.newFixedThreadPool(32);

    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executorService.submit(
          () -> {
            try {
              redissonLockCouponFacade.decreasePromotionCoupon("v5F5-4125-WXHz", 1L);
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await();

    Coupon foundCoupon = couponService.getCoupon("v5F5-4125-WXHz");
    assertThat(foundCoupon.getIssueLimit()).isNotEqualTo(0L);
  }
}
