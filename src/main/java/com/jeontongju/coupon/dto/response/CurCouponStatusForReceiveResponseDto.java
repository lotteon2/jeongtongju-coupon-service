package com.jeontongju.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CurCouponStatusForReceiveResponseDto {

  private Boolean isSoldOut;
  private Boolean isOpen;

  public void toggleIsSoldOut() {
    this.isSoldOut = !this.isSoldOut;
  }

  public void toggleIsOpen() {
    this.isOpen = !this.isOpen;
  }
}
