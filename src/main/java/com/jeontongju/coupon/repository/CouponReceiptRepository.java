package com.jeontongju.coupon.repository;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.domain.CouponReceipt;
import com.jeontongju.coupon.domain.CouponReceiptId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponReceiptRepository extends JpaRepository<CouponReceipt, CouponReceiptId> {

  @Query(
      "SELECT cr FROM CouponReceipt cr WHERE cr.id.consumerId = :consumerId AND cr.id.coupon = :coupon")
  Optional<CouponReceipt> findByCouponReceiptId(
      @Param("consumerId") Long consumerId, @Param("coupon") Coupon coupon);

  @Query("SELECT cr FROM CouponReceipt cr WHERE cr.id.consumerId = :consumerId")
  List<CouponReceipt> findByConsumerId(@Param("consumerId") Long consumerId);

  @Query("SELECT cr FROM CouponReceipt cr WHERE cr.id.consumerId = :consumerId")
  Page<CouponReceipt> findByConsumerId(@Param("consumerId") Long consumerId, Pageable pageable);

  @Query("SELECT cr FROM CouponReceipt cr WHERE cr.id.consumerId = :consumerId AND cr.isUse = :isUse")
  List<CouponReceipt> findByConsumerIdAndIsUse(@Param("consumerId") Long consumerId, @Param("isUse") boolean isUse);
}
