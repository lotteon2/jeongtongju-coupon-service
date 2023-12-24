package com.jeontongju.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.domain.CouponReceipt;
import com.jeontongju.coupon.dto.response.CurCouponStatusForReceiveResponseDto;
import com.jeontongju.coupon.exception.CouponNotFoundException;
import com.jeontongju.coupon.facade.RedissonLockCouponFacade;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Assertions;
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
              redissonLockCouponFacade.decrease("v5F5-4125-WXHz", 1L);
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await();

    Coupon coupon = couponService.getCoupon("v5F5-4125-WXHz");
    assertThat(coupon.getIssueLimit()).isEqualTo(0L);
  }

  @Test
  @DisplayName("매일 5시 이후, 프로모션 쿠폰 수령 시, 해당 쿠폰의 재고를 차감하고, 쿠폰 수령 테이블에 기록할 수 있다.")
  public void 오후5시이후_쿠폰수령_요청() {

    String PROMOTION_COUPON_CODE = "v5F5-4125-WXHz";
    Long receiverId = 1L;
    couponService.receivePromotionCoupon(receiverId);

    Coupon foundCoupon = couponService.getCoupon(PROMOTION_COUPON_CODE);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime after5PM =
        LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 17, 0);
    if (!now.isBefore(after5PM)) {
      CouponReceipt couponReceipt = couponService.getCouponReceipt(receiverId, foundCoupon);

      assertThat(couponReceipt.getId().getCoupon().getCouponCode())
          .isEqualTo(PROMOTION_COUPON_CODE);
      assertThat(couponReceipt.getId().getConsumerId()).isEqualTo(receiverId);
      assertThat(couponReceipt.getIsUse()).isEqualTo(false);

      assertThat(foundCoupon.getIssueLimit()).isEqualTo(99L);
    }
  }

  @Test
  @DisplayName("매일 오후 5시 이전에 프로모션 쿠폰 수령 요청 시, 쿠폰 수령을 할 수 없으며, 반환 DTO에 isOpen: false를 넘겨준다")
  public void 오후5시이전_쿠폰수령_요청() {

    String PROMOTION_COUPON_CODE = "v5F5-4125-WXHz";
    Long receiverId = 1L;
    CurCouponStatusForReceiveResponseDto curCouponStatusForReceiveResponseDto =
        couponService.receivePromotionCoupon(receiverId);

    Coupon foundCoupon = couponService.getCoupon(PROMOTION_COUPON_CODE);

    Assertions.assertThrows(
        CouponNotFoundException.class,
        () -> {
          couponService.getCouponReceipt(receiverId, foundCoupon);
        });

    assertThat(curCouponStatusForReceiveResponseDto.getIsOpen()).isEqualTo(false);
  }

  @Test
  @DisplayName("프로모션 쿠폰 재고가 없다면 쿠폰을 더이상 수령할 수 없고, 반환 DTO에 isSoldOut: true를 넘겨준다")
  public void 재고가_떨어진_이후_쿠폰수령_요청() {

    String PROMOTION_COUPON_CODE = "v5F5-4125-WXHz";
    Long receiverId = 1L;

    Coupon foundCoupon = couponService.getCoupon(PROMOTION_COUPON_CODE);
    foundCoupon.assignIssuedLimit(0L);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime after5PM =
        LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 17, 0);
    if (!now.isBefore(after5PM)) {
      CurCouponStatusForReceiveResponseDto curCouponStatusForReceiveResponseDto =
          couponService.receivePromotionCoupon(receiverId);

      assertThat(curCouponStatusForReceiveResponseDto.getIsSoldOut()).isEqualTo(true);
    }
  }
}
