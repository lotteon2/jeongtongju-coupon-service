package com.jeontongju.coupon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderPriceForCheckValidRequestDto {

  @NotNull(message = "총 주문 금액 미포함") private Long totalAmount;
}
