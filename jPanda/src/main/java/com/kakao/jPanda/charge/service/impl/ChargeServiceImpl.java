package com.kakao.jPanda.charge.service.impl;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kakao.jPanda.charge.dao.ChargeDao;
import com.kakao.jPanda.charge.domain.ChargeDto;
import com.kakao.jPanda.charge.domain.CouponDto;
import com.kakao.jPanda.charge.domain.CouponUseDto;
import com.kakao.jPanda.charge.domain.PaginationDto;
import com.kakao.jPanda.charge.domain.PaymentDto;
import com.kakao.jPanda.charge.service.ChargeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChargeServiceImpl implements ChargeService {

	private final ChargeDao chargeDao;
	
	public ChargeServiceImpl(ChargeDao chargeDao) {
		this.chargeDao = chargeDao;
	}
	
	 //밤부 충전
	 @Override
		public int addCharge(ChargeDto chargeDto) {
			log.info("[addCharge] Start...");
			log.info("[addCharge] chargeDto.toString() : " + chargeDto.toString());
			
			if (chargeDto.getCouponCode() == "0") {
				chargeDto.setCouponCode(null);
			}
			
			//coupon_use insert
			int insertCouponUse = chargeDao.insertCouponUse(chargeDto);
			log.info("[addCharge] insertCouponUse 결과값 : {}", insertCouponUse);
			
			log.info("[addCharge] chargeDto.getCouponCode() : " + chargeDto.getCouponCode());
			int resultInsertCharge = chargeDao.insertCharge(chargeDto);
			
			if(insertCouponUse > 0) {
				log.info("[addCharge] insertCouponUse 삽입 완료");
			} else {
				log.info("[addCharge] insertCouponUse 삽입 안함(미삽입 or 오류)");
			}
			
			log.info("[addCharge] DAO에서 반환받은 resultInsertCharge-> {}", resultInsertCharge);
			return resultInsertCharge;
			
		}
	 
	 
	// 사용가능한 쿠폰 체크
	@Override
	public int checkAvailableCoupon(CouponUseDto couponUseDto) {
		log.info("[checkAvailableCoupon] selectedcouponUseDto Start...");
		boolean isAvailable = false;	
		boolean isValidPeriod = false;
		int returnResult = 0;
		
		// selectCouponUse가 있으면 사용 불가한 쿠폰  boolean isAvailable 검증
		CouponUseDto selectedcouponUseDto = chargeDao.selectCouponUse(couponUseDto);
		log.info("[checkAvailableCoupon] selectedcouponUseDto -> {}", selectedcouponUseDto);
		
		//DTO(memberId, couponCode)가 Coupon_Use 테이블에 존재하면 사용 불가 / 존재하지 않으면 사용 가능
		if(selectedcouponUseDto == null) {
			isAvailable = true;
		} 
		
		// true는 isAvailable에 없는 쿠폰
		if(isAvailable == true) {
			
			// 해당 쿠폰이 기한남아있는지 확인 boolean isAvailable 검증
			CouponDto selectedCouponDto = chargeDao.selectCouponByCouponCode(couponUseDto.getCouponCode());
			log.info("CouponDto에 할당된 [checkAvailableCoupon] selectedCouponDto -> "+selectedCouponDto);
			
			// coupon TB에 쿠폰이 없는경우 isValidPeriod = false
			if(selectedCouponDto == null) {
				isValidPeriod = false;
			} else {
				
				Timestamp issueDate = selectedCouponDto.getIssueDate();
				long currentTime = System.currentTimeMillis();		// Long
				Timestamp timestamp = new Timestamp(currentTime);
				Timestamp expireDate = selectedCouponDto.getExpireDate();
				log.info("issueDate -> " + issueDate);
				log.info("Current Time Stamp: " + timestamp);
				log.info("expireDate -> " + expireDate);
				
				
				if(currentTime <= expireDate.getTime() && currentTime >= issueDate.getTime()) {
					isValidPeriod = true;
				} else {
					isValidPeriod = false;
				}
				
				log.info("[checkAvailableCoupon] isAvailable -> "+isAvailable);
				log.info("[checkAvailableCoupon] isValidPeriod -> "+isValidPeriod);

			}
	
		}
		 
		// isAvailable -> true, isValidPeriod -> true인 경우 , 쿠폰 사용이 가능한 상태 1을 반환
		if  (isAvailable && isValidPeriod) {
			returnResult = 1;
		}   else {
			returnResult = 0;
		}
		
		log.info("[checkAvailableCoupon] returnResult->"+returnResult);
		return returnResult;
	}

	@Override
	public Long getAvailAmountCoupon(CouponUseDto couponUseDto) {
		log.info("[getAvailAmountCoupon] Start...");
		log.info("[getAvailAmountCoupon] chargeDto.toString() -> {}", couponUseDto.toString());
		
		Long findAvailAmountCoupon = chargeDao.selectAvailAmountCoupon(couponUseDto);
		log.info("[getAvailAmountCoupon] findAvailAmountCoupon -> " + findAvailAmountCoupon);
		
		return findAvailAmountCoupon;
	}
	
	@Override
	public Long findTotalBambooByMemberId(String memberId) {
		
		Long findBambooChargeAmountByMemberId = chargeDao.selectChargeBambooAmountByMemberId(memberId);
		Long findBambooUseAmountByMemberId    = chargeDao.selectBambooUseAmountByMemberId(memberId);
		Long findTalentRefundAmountByMemberId = chargeDao.selectTalentRefundAmountByMemberId(memberId);
		log.info("[findTotalBambooByMemberId] findBambooChargeAmountMemberId -> {}", findBambooChargeAmountByMemberId);
		log.info("[findTotalBambooByMemberId] findBambooUseAmountMemberId    -> {}", findBambooUseAmountByMemberId);
		log.info("[findTotalBambooByMemberId] findTalentRefundAmountMemberId -> {}", findTalentRefundAmountByMemberId);
		
		Long foundTotalBambooByMemberId = findBambooChargeAmountByMemberId + findTalentRefundAmountByMemberId - findBambooUseAmountByMemberId;
		log.info("[findTotalBambooByMemberId] foundTotalBambooByMemberId  -> {}", foundTotalBambooByMemberId);
		
		return foundTotalBambooByMemberId;
	}

	@Override
	public List<PaymentDto> findPaymentList() {
		
		List<PaymentDto> selectPaymentList = null;
		log.info("[findPaymentList] Start...");
		
		selectPaymentList = chargeDao.selectPaymentList();
		log.info("[findPaymentList] selectPaymentList.size() -> {}", selectPaymentList.size());
		
		return selectPaymentList;
	}

	@Override
	public int totalChargeCntByChargerId(String chargerId) {
		log.info("[totalChargeCntByChargerId] Start...");
		int totalChargeCntByChargerId = chargeDao.totalChargeCntByChargerId(chargerId);
		log.info("[totalChargeCntByChargerId] count -> {}", totalChargeCntByChargerId);
		
		return totalChargeCntByChargerId;
	}

	@Override
	public Map<String, Object> findchargeHistoryMapByPagination(PaginationDto paginationDto) {
		Map<String, Object> chargeHistoryMapByPagination = new HashMap<String, Object>();
		log.info("[findchargeHistoryMapByPagination] Start...");
		String chargeId = paginationDto.getChargerId();
		int totalCount = chargeDao.totalChargeCntByChargerId(chargeId);
		log.info("[findchargeHistoryMapByPagination] totalCount -> {}", totalCount);
		
		paginationDto.setTotalCount(totalCount);
		
		List<ChargeDto> chargeListByPagination = chargeDao.selectChargeListByPagination(paginationDto);
		log.info("[findchargeHistoryMapByPagination] chargeListByPagination -> {}", chargeListByPagination.size());
		
		chargeHistoryMapByPagination.put("chargeList", chargeListByPagination);
		chargeHistoryMapByPagination.put("pagination", paginationDto);
		
		return chargeHistoryMapByPagination;
	}


}
