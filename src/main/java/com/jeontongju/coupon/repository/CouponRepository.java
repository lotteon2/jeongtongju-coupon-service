package com.jeontongju.coupon.repository;

import com.jeontongju.coupon.domain.Coupon;
import io.github.bitbox.bitbox.enums.CouponTypeEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, String> {

  Optional<Coupon> findByCouponCode(String couponCode);

  Optional<Coupon> findByCouponName(CouponTypeEnum couponTypeEnum);

  List<Coupon> findByCouponNameOrderByIssuedAtDesc(
      CouponTypeEnum couponTypeEnum, Pageable pageable);
}
