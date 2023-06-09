package com.kakao.jPanda.trade.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakao.jPanda.common.exception.CustomException;
import com.kakao.jPanda.common.exception.ErrorCode;
import com.kakao.jPanda.trade.domain.StatDto;
import com.kakao.jPanda.trade.domain.TalentDto;
import com.kakao.jPanda.trade.domain.TradeDto;
import com.kakao.jPanda.trade.service.TradeService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/trade")
public class TradeController {
	
	private TradeService tradeService;
	
	@Autowired
	public TradeController(TradeService tradeService) {
		this.tradeService = tradeService;
	}
	
	/**
	 * 페이지 로딩시 Stat창 출력
	 * @param session
	 * @param model
	 * @return trade페이지 url 매핑
	 */
	@GetMapping("")
	public String statList(HttpSession session, Model model) {
		String memberId = (String)session.getAttribute("memberId");
		log.info("statList id check : " + memberId);
		List<StatDto> statList = tradeService.findStatListByMemberId(memberId);
		model.addAttribute("statList", statList);
		return "trade/trade";
	}
	
	/**
	 * 전체/판매/구매/환불 필터링 Btn
	 * status에 따른 다른 service 호출
	 * @param session
	 * @param model
	 * @param status
	 * @return 
	 */
	@GetMapping("/trades")
	@ResponseBody
	public List<TradeDto> tradeList(HttpSession session, Model model, @RequestParam(value = "list-type") String listType) {
		String memberId = (String)session.getAttribute("memberId");
		log.info("tradeList id check : " + memberId);
		log.info("tradeList listType : " + listType);
		
		return tradeService.findTradeListByMemberId(memberId, listType);
	}
	
	/**
	 * 재등록, 판매종료 Btn
	 * talent 통합 Update
	 * @param talentNo
	 * @param talentDto
	 * @return resultMessage
	 */
	@PutMapping("/talents/{talent-no}")
	@ResponseBody
	public ResponseEntity<Object> tradesByTalentNo(@PathVariable(name = "talent-no") String talentNo, 
								   @RequestBody TalentDto talentDto) {
		log.info("tradeModifyStatusByTalentNo talentNo, status : " + talentNo + ", " + talentDto);
		Integer result = tradeService.modifyTalentByTalentNo(talentNo, talentDto);
		log.info("tradeModifyStatusByTalentNo result : " + result);
		
		if (result == 1) {
			return ResponseEntity.ok(talentNo);
		} else if (result == 0){
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(talentNo);
		} else {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * 환전신청 Btn
	 * @param talentNo
	 * @return resultMessage
	 */
	@PostMapping("/exchanges/{talent-no}")
	@ResponseBody
	public ResponseEntity<Object> exchangeAddByTalentNo(@PathVariable(name = "talent-no") String talentNo) {
		log.info("exchangeAddByTalentNo purchaseNo : " + talentNo);
		TalentDto talentDto = tradeService.findTalentByTalentNo(talentNo);
		if (talentDto == null) {
			return ResponseEntity.noContent().build();
		} else {
			Integer result = tradeService.addExchangeByTalentNo(talentDto);
			log.info("exchangeAddByTalentNo result : " + result);
			if (result == 1) {
				return ResponseEntity.ok(talentNo);
			} else if(result == 0) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(talentNo);
			} else {
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
			}
			
		}
		
	}
	
	/**
	 * 환전 재신청 Btn
	 * exchange TB exchange_status '반려' → '검토중' 
	 * @param talentNo
	 * @return resultMessage
	 */
	@PutMapping("/exchanges/{talent-no}/status")
	@ResponseBody
	public ResponseEntity<Object> exchangeStatusModifyByTalentNo(@PathVariable(name = "talent-no") String talentNo) {
		log.info("exchangeStatusModifyByTalentNo talentNo : " + talentNo);
		Integer result = tradeService.modifyExchangeStatusByTalentNo(talentNo);
		log.info("exchangeStatusModifyByTalentNo result : " + result);
		
		if (result == 1) {
			return ResponseEntity.ok(talentNo);
		} else {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 환불 신청 Btn
	 * @param session
	 * @param tradeDto
	 * @return resultMessage
	 */
	@PostMapping("/refund")
	@ResponseBody
	public ResponseEntity<Object> refundAdd(HttpSession session, @RequestBody TradeDto tradeDto) {
		String memberId = (String)session.getAttribute("memberId");
		log.info("refundAdd tradeListDto : " + tradeDto.toString());
		Integer result = tradeService.addRefund(memberId, tradeDto);
		log.info("refundAdd resultMessage : " + result);
		
		if (result == 1) {
			return ResponseEntity.ok(tradeDto.getTalentNo());
		} else {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 환불 신청 취소 Btn
	 * @param session
	 * @param tradeDto
	 * @return resultMessage
	 */
	@DeleteMapping("/refunds/{refund-purchase-no}")
	@ResponseBody
	public ResponseEntity<Object> refundRemoveByrefundPurchaseNo(@PathVariable(name = "refund-purchase-no") String refundPurchaseNo) {
		log.info("refundRemoveByrefundPurchaseNo purchaseNo : " + refundPurchaseNo);
		Integer result = tradeService.removeRefundByrefundPurchaseNo(refundPurchaseNo);
		log.info("refundRemoveByrefundPurchaseNo result : " + result);
		
		if (result == 1) {
			return ResponseEntity.ok(result);
		} else if (result == 0){
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(refundPurchaseNo);
		} else {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		
	}
	
}//end class