package com.kakao.jPanda.charge.dao;

import java.util.List;

import com.kakao.jPanda.charge.domain.ChargeDto;
import com.kakao.jPanda.charge.domain.CouponDto;
import com.kakao.jPanda.charge.domain.CouponUseDto;
import com.kakao.jPanda.charge.domain.PaginationDto;
import com.kakao.jPanda.charge.domain.PaymentDto;

public interface ChargeDao {

	int insertCouponUse(ChargeDto chargeDto);
	
	int insertCharge(ChargeDto chargeDto);

	CouponUseDto selectCouponUse(CouponUseDto couponUseDto);

	Long selectAvailAmountCoupon(CouponUseDto couponUseDto);

	CouponDto selectCouponByCouponCode(String couponCode);

	Long selectChargeBambooAmountByMemberId(String memberId);

	Long selectBambooUseAmountByMemberId(String memberId);

	Long selectTalentRefundAmountByMemberId(String memberId);

	List<PaymentDto> selectPaymentList();

	int totalChargeCntByChargerId(String chargerId);

	List<ChargeDto> selectChargeListByPagination(PaginationDto paginationDto);


	
}
