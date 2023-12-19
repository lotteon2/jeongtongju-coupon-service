package com.jeontongju.coupon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CouponServiceTests {

  @Autowired private CouponService couponService;
}
