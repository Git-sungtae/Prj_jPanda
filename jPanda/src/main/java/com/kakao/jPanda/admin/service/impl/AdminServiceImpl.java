package com.kakao.jPanda.admin.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kakao.jPanda.admin.dao.AdminDao;
import com.kakao.jPanda.admin.domain.CompanySalesDto;
import com.kakao.jPanda.admin.domain.CouponDto;
import com.kakao.jPanda.admin.domain.ExchangeDto;
import com.kakao.jPanda.admin.domain.NoticeDto;
import com.kakao.jPanda.admin.domain.PaginationDto;
import com.kakao.jPanda.admin.domain.ReportDto;
import com.kakao.jPanda.admin.domain.TalentDto;
import com.kakao.jPanda.admin.domain.TalentRefundDto;
import com.kakao.jPanda.admin.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
	private final AdminDao adminDao;
	
	//notice
	@Override
	public Map<String, Object> findNoticeByPagination(PaginationDto paginationDto) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		int totalCount = adminDao.selectNoticeForTotalCount();
		paginationDto.setTotalCount(totalCount);
		
		List<NoticeDto> noticeList = adminDao.selectNoticeByPagination(paginationDto);
		
		returnMap.put("noticeList", noticeList);
		returnMap.put("pagination", paginationDto);
		
		return returnMap;
	}

	
	/*
	 * 	insert결과에 따라 view에 alert띄우는 기능
	 */
	@Override
	public String addNotice(NoticeDto notice) {
		log.info("Service addNotice() start");
		String resultStr = "";
		
		int result = adminDao.insertNotice(notice);
		
		if(result > 0) {
			resultStr = "<script>alert('공지사항 적용이 완료되었습니다.'); location.href='/admin/notice';</script>";
		} else {
			resultStr = "<script>alert('공지사항 적용 오류'); history.back();</script>";
		}
		return resultStr;
	}
	
	/*
	 * 	noticeNo에 맞는 noticeDetail조회 기능
	 * 	parameter 타입이 Long이기 때문에 Long타입 parsing후 넘김
	 */
	@Override
	public NoticeDto findNoticeByNoticeNo(String noticeNo) {
		log.info("Service findNoticeByNoticeNo() start");
		NoticeDto notice = adminDao.selectNoticeByNoticeNo(Long.parseLong(noticeNo));
		
		return notice;
	}

	/*
	 * 	noticeDetail에서 수정버튼을 눌러 수정 후 성공시 화면에 alert띄우는 기능
	 */
	@Override
	public String modifyNotice(NoticeDto notice) {
		log.info("Service modifyNotice() start");
		String resultStr = "";
		
		int result = adminDao.updateNotice(notice);
		
		if(result > 0) {
			resultStr = "<script>alert('공지사항 수정이 완료되었습니다.'); location.href='/admin/notice'; </script>";
		} else {
			resultStr = "<script>alert('공지사항 수정 오류'); history.back(); </script>";
		}
		return resultStr;
	}
	
	//exchange
	@Override
	public Map<String, Object> findExchangeByPagination(PaginationDto paginationDto) {
		Map<String, Object> returMap = new HashMap<String, Object>();
		
		int totalCount = adminDao.selectExchangeForTotalCount();
		paginationDto.setTotalCount(totalCount);
		
		List<ExchangeDto> exList = adminDao.selectExchangeByPagination(paginationDto);
		
		returMap.put("exList", exList);
		returMap.put("pagination", paginationDto);
		
		return returMap;
	}

	/*
	 * 	체크박스에서 체크된 value인 exchangeNo를 배열로 받아 반복문으로 꺼내 parsing과 set후 sql작업하는 기능
	 * 	status에 value를 업데이트 될 parameter로 받아서 로직 수행
	 * 	통합 후 DTO로 parameter를 받아서 처리할 수 있도록 수정할 예정
	 */
	@Override
	public int modifyExchangeStatusByExchangeNos(List<ExchangeDto> exchangeDto) {
		log.info("Service modifyExchangeStatusByExchangeNos() start");
		log.info("exchangeDto : "+exchangeDto);
		int result = 0;
		
		for(int i = 0; i < exchangeDto.size(); i++) {
			ExchangeDto paramDto = exchangeDto.get(i);
			log.info("Exchange Service paramDto : "+paramDto.toString());
			
			if(paramDto.getExchangeStatus().equals("환전완료")) {
				result = adminDao.updateExchange(paramDto);
			} else {
				result = adminDao.updateExchange(paramDto);
			}
		}
		
		return result;
	}
	
	//coupon
	/*
	 * 	쿠폰 생성하기를 눌렀을때, UUID객체로 랜덤의 uuid를 생성하고, DB에 쿠폰 번호 데이터 사이즈와 타입에 맞게
	 * 	toString, substring작업을 한 뒤에 PK검증을 위해 조회하고, 결과에 따라 해당화면 입력부에 생성이 되거나 중복 alert띄우는 기능
	 * 	통합 후 데이터 전송 방식과 로직을 조금 손 볼 예정
 	 */
	@Override
	public Map<String, Object> findCouponListByPagination(PaginationDto paginationDto) {
		log.info("Coupon Service findCouponListByPagination() start");
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		int totalCount = adminDao.selectCouponForTotalCount();
		paginationDto.setTotalCount(totalCount);
		log.info("pagination : "+paginationDto);
		
		List<CouponDto> couponList = adminDao.selectCouponsByPagination(paginationDto);
		
		returnMap.put("couponList", couponList);
		returnMap.put("pagination", paginationDto);
		
		return returnMap;
	}

	@Override
	public String generateCouponNo(){
		log.info("Service generateCouponNo() start");
		List<CouponDto> couponList = null;
		String couponNo = null;
		UUID uuid = null;
		boolean result = false;
		
		do {
			uuid = UUID.randomUUID();
			couponNo = uuid.toString().substring(0, 6);
			log.info("couponNo : "+couponNo);
			couponList = adminDao.selectCouponList();
			log.info("couponList : "+couponList);
			
			for(CouponDto cn : couponList) {
				if(cn.getCouponCode().equals(couponNo)) {
					result = false;
				} else {
					result = true;
				}
			}
		} while(!result);
		
		return couponNo;
	}
	
	/*
	 * 	위에 로직을 통해 생성된 쿠폰 번호를 insert하는 기능
	 * 	사용기한에 대한 논의가 부족해 통합 후 논의를 통해 사용기한 정하는 로직 구현 예정
	 */
	@Override
	public Map<String, Integer> addCoupon(CouponDto couponDto) {
		log.info("Service addCoupon() start");
		log.info("Service couponDto : " + couponDto.toString());
		
		Map<String, Integer> returnMap = new HashMap<String, Integer>();
		int valid = 0;
		
		if(couponDto.getCouponValue() == null) {
			valid = -1;
			
		} else {
			valid = adminDao.insertCoupon(couponDto);
		}
		
		returnMap.put("valid", valid);
		
		return returnMap;
	}
	
	//company-sales
	/*
	 * 	화면에 날짜 입력란에서 startDate와 endDate를 입력받아 그래프 api를 통해 매출을 출력하는 기능
	 * 	startDate가 1일로 설정되었을때, YYYY/MM 형태의 매출그래프 출력
	 * 	startDate와 endDate가 1이상으로 설정되었을때, YYYY/MM/DD 형태의 매출 그래프 출력
	 * 	통합 후 검증된 더미데이터로 sql검증 필요
	 */
	@Override
	public List<CompanySalesDto> findCompanySalesByStartDateAndEndDate(Timestamp startDate, Timestamp endDate, String periodicData) {
		log.info("Service findCompanySalesByYears() start");
		CompanySalesDto companySalesDto = new CompanySalesDto();
		List<CompanySalesDto> csList = null;
		
		if(periodicData.equals("Y")) {
			companySalesDto.setStartDate(startDate);
			companySalesDto.setEndDate(endDate);
			csList = adminDao.selectCompanySalesByYYYYDate(companySalesDto);
		} else if(periodicData.equals("M")) {
			companySalesDto.setStartDate(startDate);
			companySalesDto.setEndDate(endDate);
			csList = adminDao.selectCompanySalesByMMDate(companySalesDto);
		} else {
			companySalesDto.setStartDate(startDate);
			companySalesDto.setEndDate(endDate);
			csList = adminDao.selectCompanySalesByDDDate(companySalesDto);
		}
		
		List<CompanySalesDto> returnList = new ArrayList<CompanySalesDto>();
		
		for(int i = 0; i < csList.size(); i++) {
			CompanySalesDto returnDto = csList.get(i);
			
			Long bcCount = returnDto.getBcCount();
			Long exCount = returnDto.getExCount();
			String period = returnDto.getPeriod();
			
			bcCount = bcCount == null ? 0L : bcCount;
			exCount = exCount == null ? 0L : exCount;
			
			log.info("CompanySales Service getCout() : "+bcCount);
			log.info("CompanySales Service getExCount() : "+exCount);
			log.info("CompanySales Service getPeriod() : "+period);
			
			returnDto.setBcCount(bcCount);
			returnDto.setExCount(exCount);
			returnDto.setPeriod(period);
			
			returnList.add(returnDto);
			}
			
			return returnList;
		}
	
	//talent
	@Override
	public Map<String, Object> findTalentByPagination(PaginationDto paginationDto) {
		log.info("Talent Service findTalentByPagination() start");
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		int totalCount = adminDao.selectTalentForTotalCount();
		paginationDto.setTotalCount(totalCount);
		
		List<TalentDto> talentList = adminDao.selectTalentByPagination(paginationDto);
		
		returnMap.put("talentList", talentList);
		returnMap.put("pagination", paginationDto);
		
		return returnMap;
	}
	
	/*
	 * 	판매등록 신청건에 대한 게시상태를 업데이트 하는 기능
	 */
	@Override
	public int modifyTalentByTalentNos(List<TalentDto> talentDto) {
		log.info("Talent Service modifyTalentBySellerIds() start");
		int result = 0;
		
		for(int i = 0; i < talentDto.size(); i++) {
			TalentDto paramDto = talentDto.get(i);
			result = adminDao.updateTalentByTalentNos(paramDto);
		}
		
		return result;
	}
	
	@Override
	public TalentDto findTalentByTalentNo(Long talentNo) {
		log.info("Talent Service findTalentByTalentNo() start");
		return adminDao.selectTalentByTalentNo(talentNo);
	}

	//talent-refund
	@Override
	public Map<String, Object> findTalentRefundByPagination(PaginationDto paginationDto) {
		log.info("Talent Refund Service findTalentRefundByPagination() start");
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		int totalCount = adminDao.selectTalentRefundForTotalCount();
		paginationDto.setTotalCount(totalCount);
		
		List<TalentRefundDto> refundList = adminDao.selectTalentRefunByPagination(paginationDto);
		
		returnMap.put("refundList", refundList);
		returnMap.put("pagination", paginationDto);
		
		return returnMap;
	}

	/*
	 *  체크박스로 체크된 value(purchaseNo)에 대한 상태를 업데이트,
	 *  버튼의 value를 parameter로 받아 해당 버튼의 value로 업데이트 하는 기능
	 */
	@Override
	public int modifyTalentRefundByPurchaseNosAndStatus(List<TalentRefundDto> talentRefundDto) {
		log.info("Talent Refund Service modifyTalentRefundByPurchaseNosAndStatus() start");
		int result = 0;
		
		for(int i = 0; i < talentRefundDto.size(); i++) {
			TalentRefundDto paramDto = talentRefundDto.get(i);
			log.info("TalentRefund Service paramDto : "+paramDto.toString());
			
			if(paramDto.getRefundStatus().equals("환불완료")) {
				result = adminDao.updateTalentRefundToSuccessByPurchaseNosAndStatus(paramDto);
			} else {
				result = adminDao.updateTalentRefundToCompanionByPurchaseNosAndStatus(paramDto);
			}
		}
		return result;
	}
	
	//report
	@Override
	public List<ReportDto> findReport() {
		log.info("Report Service findReport() start");
		List<ReportDto> reportList = adminDao.selectReport();
		return reportList;
	}

	@Override
	public List<ReportDto> findReportByBlackId(String blackId) {
		log.info("Report Service findReportByBlackId() start");
		List<ReportDto> reportList = adminDao.selectReportByBlackId(blackId);
		log.info("reportList : "+reportList);
		
		return reportList;
	}

	@Override
	public int modifyReportByMemberId(String memberId) {
		log.info("Report Service modifyReportByMemberId() start");
		
		return adminDao.updateReportByMemberId(memberId);
	}

}
