package com.jeontongju.coupon.domain;

import java.sql.Timestamp;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Coupon {

  @Id
  @Column(name = "coupon_code", nullable = false)
  private String couponCode;

  @Column(name = "coupon_name", nullable = false)
  private String couponName;

  @Column(name = "discount_amount", nullable = false)
  private Long discountAmount;

  @Column(name = "issue_limit", nullable = false)
  private Long issueLimit;

  @Column(name = "issued_at", nullable = false)
  private Timestamp issuedAt;

  @Column(name = "expired_at", nullable = false)
  private Timestamp expiredAt;

  @Column(name = "min_order_price", nullable = false)
  private Long minOrderPrice;

  public void decrease(Long quantity) {
    this.issueLimit -= quantity;
  }

  public void assignIssuedLimit(Long issueLimit) {
    this.issueLimit = issueLimit;
  }
}
