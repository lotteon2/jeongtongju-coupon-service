package com.jeontongju.coupon.base;

import com.jeontongju.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

  private final CouponService couponService;

  @Scheduled(cron = "0 0 20 * * *")
  public void issuePromotionCoupons() {
    log.info("Scheduler's issuePromotionCoupons executes..");
    couponService.issuePromotionCoupons();
  }
}
