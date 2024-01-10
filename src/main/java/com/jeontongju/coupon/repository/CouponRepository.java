package com.jeontongju.coupon.repository;

import com.jeontongju.coupon.domain.Coupon;
import java.util.Optional;

import io.github.bitbox.bitbox.enums.CouponTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, String> {

  Optional<Coupon> findByCouponCode(String couponCode);

  Optional<Coupon> findByCouponName(CouponTypeEnum couponTypeEnum);
}
