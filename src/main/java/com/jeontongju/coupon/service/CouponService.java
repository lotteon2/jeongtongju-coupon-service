package com.jeontongju.coupon.service;

import com.jeontongju.coupon.domain.Coupon;
import com.jeontongju.coupon.domain.CouponReceipt;
import com.jeontongju.coupon.dto.request.OrderPriceForCheckValidRequestDto;
import com.jeontongju.coupon.dto.response.AvailableCouponInfoForSummaryNDetailsResponseDto;
import com.jeontongju.coupon.dto.response.CouponInfoForSingleInquiryResponseDto;
import com.jeontongju.coupon.exception.*;
import com.jeontongju.coupon.mapper.CouponMapper;
import com.jeontongju.coupon.repository.CouponReceiptRepository;
import com.jeontongju.coupon.repository.CouponRepository;
import com.jeontongju.coupon.utils.CustomErrMessage;
import com.jeontongju.coupon.utils.PaginationManager;
import io.github.bitbox.bitbox.dto.ConsumerRegularPaymentsCouponDto;
import io.github.bitbox.bitbox.dto.OrderCancelDto;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import io.github.bitbox.bitbox.dto.UserCouponUpdateDto;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;
  private final CouponReceiptRepository couponReceiptRepository;
  private final CouponMapper couponMapper;
  private final PaginationManager<CouponInfoForSingleInquiryResponseDto> paginationManager;

  private static final String PROMOTION_COUPON_CODE = "v5F5-4125-WXHz";

  /**
   * 주문 및 결제 확정을 위한 쿠폰 사용
   *
   * @param orderInfoDto
   */
  @Transactional
  public void deductCoupon(OrderInfoDto orderInfoDto) {

    UserCouponUpdateDto userCouponUpdateDto = orderInfoDto.getUserCouponUpdateDto();

    if (userCouponUpdateDto.getCouponCode() != null) {
      Coupon foundCoupon = getCoupon(userCouponUpdateDto.getCouponCode());

      checkCouponInfo(userCouponUpdateDto);

      CouponReceipt foundCouponReceipt =
          getCouponReceipt(userCouponUpdateDto.getConsumerId(), foundCoupon);

      // 쿠폰 사용 처리
      foundCouponReceipt.deductCoupon();
    }
  }

  /**
   * 주문 및 결제 로직에서 에러 발생 시, 쿠폰 사용 롤백
   *
   * @param orderInfoDto
   */
  @Transactional
  public void rollbackCouponUsage(OrderInfoDto orderInfoDto) {

    UserCouponUpdateDto userCouponUpdateDto = orderInfoDto.getUserCouponUpdateDto();

    if (userCouponUpdateDto.getCouponCode() != null) {
      Coupon foundCoupon = getCoupon(userCouponUpdateDto.getCouponCode());
      CouponReceipt foundCouponReceipt =
          getCouponReceipt(userCouponUpdateDto.getConsumerId(), foundCoupon);

      foundCouponReceipt.rollbackCoupon();
    }
  }

  /**
   * 쿠폰 유효성 검증
   *
   * @param userCouponUpdateDto
   */
  public void checkCouponInfo(UserCouponUpdateDto userCouponUpdateDto) {

    Coupon foundCoupon = getCoupon(userCouponUpdateDto.getCouponCode());

    CouponReceipt foundCouponReceipt =
        getCouponReceipt(userCouponUpdateDto.getConsumerId(), foundCoupon);

    log.info("foundCouponReceipt: " + foundCouponReceipt.getId().getCoupon().getCouponCode());
    if (foundCouponReceipt.getIsUse()) {
      log.info("이미 사용한 쿠폰");
      throw new AlreadyUseCouponException(CustomErrMessage.ALREADY_USE_COUPON);
    }

    // 쿠폰 만료 여부 확인
    if (!isValidCoupon(foundCoupon.getExpiredAt())) {
      log.info("만료된 쿠폰");
      throw new CouponExpiredException(CustomErrMessage.EXPIRED_COUPON);
    }

    // 쿠폰 코드와 할인 금액 일치 여부 확인
    if (!Objects.equals(userCouponUpdateDto.getCouponAmount(), foundCoupon.getDiscountAmount())) {
      log.info("쿠폰 코드와 할인 금액 불일치");
      throw new IncorrectCouponDiscountAmountException(
          CustomErrMessage.INCORRECT_COUPON_DISCOUNT_AMOUNT);
    }

    // 쿠폰 사용을 위한 최소 주문 금액 확인
    if (userCouponUpdateDto.getTotalAmount() < foundCoupon.getMinOrderPrice()) {
      log.info("최소 주문 금액 미달");
      throw new InsufficientMinOrderPriceException(CustomErrMessage.INSUFFICIENT_MIN_ORDER_PRICE);
    }
  }

  /**
   * 해당 쿠폰 만료 여부 확인
   *
   * @param expiredAt
   * @return Boolean
   */
  private Boolean isValidCoupon(LocalDateTime expiredAt) {

    return LocalDateTime.now().isBefore(expiredAt);
  }

  /**
   * 주문 취소 시, 해당 쿠폰 미사용 처리
   *
   * @param orderCancelDto
   */
  @Transactional
  public void refundCouponByOrderCancel(OrderCancelDto orderCancelDto) {

    Coupon foundCoupon = getCoupon(orderCancelDto.getCouponCode());
    CouponReceipt foundCouponReceipt =
        getCouponReceipt(orderCancelDto.getConsumerId(), foundCoupon);
    foundCouponReceipt.rollbackCoupon();
  }

  /**
   * Promotion 쿠폰 수령을 위한 사전 체크
   *
   * @param consumerId 로그인 한 회원 식별자
   */
  public void preCheck(Long consumerId)
      throws NotOpenPromotionCouponEventException, AlreadyReceiveCouponException {

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime after5PM =
        LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 17, 0);

    // test를 위해 주석 처리
    if (now.isBefore(after5PM)) {
      throw new NotOpenPromotionCouponEventException(
          CustomErrMessage.NOT_OPEN_PROMOTION_COUPON_EVENT);
    }

    Coupon foundCoupon = getCoupon(PROMOTION_COUPON_CODE);

    Optional<CouponReceipt> foundCouponReceipt =
        couponReceiptRepository.findByCouponReceiptId(consumerId, foundCoupon);
    if (foundCouponReceipt.isPresent()) {
      throw new AlreadyReceiveCouponException(CustomErrMessage.ALREADY_RECEIVE_COUPON);
    }
  }

  /**
   * 쿠폰 수량 차감
   *
   * @param couponCode Promotion 쿠폰 코드(식별자)
   * @param quantity 차감할 쿠폰 수량
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void decreasePromotionCoupon(String couponCode, Long quantity) {

    Coupon foundCoupon =
        couponRepository
            .findByCouponCode(couponCode)
            .orElseThrow(() -> new CouponNotFoundException(CustomErrMessage.NOT_FOUND_COUPON));

    if (foundCoupon.getIssueLimit() <= 0L) {
      //      return;
      throw new CouponExhaustedException(CustomErrMessage.EXHAUSTED_COUPON);
    }
    foundCoupon.decrease(quantity);
    couponRepository.saveAndFlush(foundCoupon);
  }

  /**
   * 쿠폰 수령 후, 수령 내역 저장
   *
   * @param couponCode 수령한 쿠폰 코드(식별자)
   * @param consumerId 로그인 한 회원 식별자
   */
  @Transactional
  public void AfterProcessing(String couponCode, Long consumerId) {

    Coupon foundCoupon = getCoupon(couponCode);
    couponReceiptRepository.save(couponMapper.toCouponReceiptEntity(foundCoupon, consumerId));
  }

  public Page<CouponInfoForSingleInquiryResponseDto> getMyCouponsForListLookup(
      Long consumerId, int page, int size, String search) {

    boolean isUsed = false;
    boolean isAvailable = false;

    if ("used".equals(search)) {
      isUsed = true;
    }

    if ("available".equals(search)) {
      isAvailable = true;
    }

    Pageable pageable = paginationManager.getPageableByCreatedAt(page, size);

    Page<CouponReceipt> foundCouponReceipts =
        couponReceiptRepository.findByConsumerId(consumerId, pageable);

    List<CouponInfoForSingleInquiryResponseDto> couponList = new ArrayList<>(); // 사용 가능한 쿠폰 내역
    List<CouponInfoForSingleInquiryResponseDto> usedCouponList = new ArrayList<>(); // 사용 불가능한 쿠폰 내역

    for (CouponReceipt couponReceipt : foundCouponReceipts) {
      Coupon foundCoupon = couponReceipt.getId().getCoupon();
      if (couponReceipt.getIsUse() || !isValidCoupon(foundCoupon.getExpiredAt())) {
        usedCouponList.add(couponMapper.toInquiryDto(foundCoupon));
        continue;
      }
      couponList.add(couponMapper.toInquiryDto(foundCoupon));
    }

    int totalSize = couponReceiptRepository.findByConsumerId(consumerId).size();

    if (!isUsed && !isAvailable) {
      return paginationManager.wrapByPage(new ArrayList<>(), pageable, totalSize);
    }

    return isUsed
        ? paginationManager.wrapByPage(usedCouponList, pageable, totalSize)
        : paginationManager.wrapByPage(couponList, pageable, totalSize);
  }

  /**
   * 주문시, 사용할 수 있는 쿠폰의 개수와 정보 가져오기
   *
   * @param consumerId
   * @param checkValidRequestDto
   * @return AvailableCouponInfoForSummaryNDetailsResponseDto
   */
  public AvailableCouponInfoForSummaryNDetailsResponseDto getAvailableCouponsWhenOrdering(
      Long consumerId, OrderPriceForCheckValidRequestDto checkValidRequestDto) {

    List<CouponReceipt> foundCouponReceipts =
        couponReceiptRepository.findByConsumerIdAndIsUse(consumerId, false);

    List<CouponInfoForSingleInquiryResponseDto> availableCouponList = new ArrayList<>();

    int totalValidCounts = foundCouponReceipts.size();
    int unavailableCounts = 0;
    for (CouponReceipt couponReceipt : foundCouponReceipts) {
      Coupon foundCoupon = couponReceipt.getId().getCoupon();
      if (!isValidCoupon(foundCoupon.getExpiredAt())) {
        totalValidCounts -= 1;
        continue;
      }

      if (checkValidRequestDto.getTotalAmount() < foundCoupon.getMinOrderPrice()) {
        unavailableCounts += 1;
        continue;
      }

      availableCouponList.add(couponMapper.toInquiryDto(foundCoupon));
    }

    return couponMapper.toSummaryNDetailsDto(
        totalValidCounts, (totalValidCounts - unavailableCounts), availableCouponList);
  }

  @Transactional
  public String giveRegularPaymentsCoupon(
      ConsumerRegularPaymentsCouponDto regularPaymentsCouponDto) {

    String generatedCouponCode = generateCouponCode();
    log.info("generatedCoupon: " + generatedCouponCode);

    Coupon issuedCoupon =
        couponRepository.save(
            couponMapper.toRegularPaymentsCouponEntity(
                generatedCouponCode, regularPaymentsCouponDto.getSuccessedAt()));

    couponReceiptRepository.save(
        couponMapper.toCouponReceiptEntity(issuedCoupon, regularPaymentsCouponDto.getConsumerId()));
    return generatedCouponCode;
  }

  @Transactional
  public void issuePromotionCoupons() {

    Coupon foundPromotionCoupon = getCoupon(PROMOTION_COUPON_CODE);
    foundPromotionCoupon.assignIssuedLimit(100L);
  }

  public String generateCouponCode() {

    final int CODE_LEN = 14;
    SecureRandom random = new SecureRandom();
    StringBuilder builder = new StringBuilder(CODE_LEN);

    final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    for (int i = 0; i < CODE_LEN; i++) {
      int randomIdx = random.nextInt(CHARACTERS.length());
      char randomChar = CHARACTERS.charAt(randomIdx);
      builder.append(randomChar);

      if ((i + 1) % 4 == 0 && (i + 1) != CODE_LEN) {
        builder.append("-");
      }
    }

    return builder.toString();
  }

  /**
   * consumerId와 coupon으로 CouponReceipt(쿠폰 수령 내역) 찾기
   *
   * @param consumerId
   * @param foundCoupon
   * @return CouponReceipt
   */
  public CouponReceipt getCouponReceipt(Long consumerId, Coupon foundCoupon) {
    return couponReceiptRepository
        .findByCouponReceiptId(consumerId, foundCoupon)
        .orElseThrow(() -> new CouponNotFoundException(CustomErrMessage.NOT_FOUND_COUPON_RECEIPT));
  }

  /**
   * couponCode로 Coupon 찾기 (공통화)
   *
   * @param couponCode
   * @return Coupon
   */
  public Coupon getCoupon(String couponCode) {

    return couponRepository
        .findByCouponCode(couponCode)
        .orElseThrow(() -> new CouponNotFoundException(CustomErrMessage.NOT_FOUND_COUPON));
  }
}
