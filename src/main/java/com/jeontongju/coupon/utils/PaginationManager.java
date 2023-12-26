package com.jeontongju.coupon.utils;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaginationManager<T> {

  public Page<T> wrapByPage(List<T> histories, Pageable pageable, Integer totalSize) {

    return new PageImpl<>(histories, pageable, totalSize);
  }

  public Pageable getPageableByCreatedAt(int page, int size) {

    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createdAt"));
    return PageRequest.of(page, size);
  }
}
