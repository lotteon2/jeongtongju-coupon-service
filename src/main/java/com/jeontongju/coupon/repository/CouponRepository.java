package com.jeontongju.coupon.repository;

import com.jeontongju.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, String> {

  Optional<Coupon> findByCouponCode(String couponCode);
}