package com.kakao.jPanda.yjh.controller;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakao.jPanda.yjh.domain.CompanySalesDto;
import com.kakao.jPanda.yjh.domain.Exchange;
import com.kakao.jPanda.yjh.domain.Notice;
import com.kakao.jPanda.yjh.domain.Talent;
import com.kakao.jPanda.yjh.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/admin")
public class AdminController {
	private final AdminService adminService;
	
	//home
	@GetMapping(value = "")
	public String home() {
		
		return "yjh/home";
	}
	
	//notice
	
	@GetMapping(value = "/notice") // ~~ /notices/{noticeNo}  @Pathvariable  ,   /notices   @Pathparam LocalDate date ?date="dsaffdsa" 
	public String noticeList(Model model) {
		log.info("Notice Controller noticeList() start");
		List<Notice> noticeList = adminService.findNotice();
		model.addAttribute("noticeList", noticeList);
		
		return "yjh/notice";
	}
	
	@GetMapping(value = "/notices/{noticeNo}")
	public String noticeDetails(@PathVariable String noticeNo, Model model) {
		log.info("Notice Controller noticeDetails() start");
		log.info("noticeNo : "+noticeNo);
		Notice notice = adminService.findNoticeByNoticeNo(noticeNo);	
		model.addAttribute("notice", notice);
		
		return "yjh/notice-content";
	}
	/*
	@GetMapping(value = "/notices") // ~~ /notices/{noticeNo}  @Pathvariable  ,   /notices   @Pathparam LocalDate date ?date="dsaffdsa" 
	public String noticeListByDate(@PathParam(value = "date") LocalDate date, Model model) {
		///service
		if(date == null) {
			//dao.selectlist();
		} else {
			//dao.selectOne(date);
		}
		
		model.addAttribute("noticeList", noticeList);
		
		return "yjh/notice";
	}
	*/
	
	@GetMapping(value = "/notice-form")
	public String noticeForm() {
		
		return "yjh/notice-form";
	}
	
	@ResponseBody
	@PostMapping(value = "/notice")
	public String noticeAdd(Notice notice) {
		log.info("Notice Controller noticeAdd() start");
		String resultStr = adminService.addNotice(notice);
		
		return resultStr;
	}
	
	@GetMapping(value = "/notice/{noticeNo}/modify-form")
	public String noticeModifyForm(@PathVariable String noticeNo, Model model) {
		log.info("Notice Controller noticeModifyForm() start");
		log.info("noticeNo : "+noticeNo);
		Notice notice = adminService.findNoticeByNoticeNo(noticeNo);
		model.addAttribute("notice", notice);
		
		return "yjh/notice-modifyFrom";
	}
	
	@ResponseBody
	@PutMapping(value = "/notice/{noticeNo}/modify")
	public String noticeModifyByNoticeNo(Notice notice) {
		log.info("Notice Controller noticeModifyByNoticeNo() start");
		log.info("notice : "+notice.toString());
		String resultStr = adminService.modifyNotice(notice);
		
		return resultStr;
	}
	
	//exchange	
	@GetMapping(value = "/exchange")
	public String exchangeList(Model model) {
		log.info("Exchange Controller exchangeList() start");
		List<Exchange> exList = adminService.findExchange();
		model.addAttribute("exList", exList);
				
		return "yjh/exchange";
	}
	
	@PutMapping(value = "/exchange")
	public String exchangeModifyByExchangeNos(@RequestParam(name = "exchangeNo") String[] exchangeNoArray, @RequestParam String status) {
		log.info("Exchange Controller exchangeModifyByExchangeNos() start");
		
		for(int i = 0; i < exchangeNoArray.length; i++) {
			log.info("exchangeNoArray : "+exchangeNoArray[i]);
		}
		
		adminService.modifyExchangeStatusByExchangeNos(exchangeNoArray, status);
		
		return "redirect:/admin";
	}
	

	/*
	@PutMapping(value = "/exchanges/{exchangeNos}") // /exchanges/{exchangeNos}?status="반려" /exchnages/{exchange.exchangeNo}
	public String exchangeModifyByExchangeNosWithStatus(@PathVariable(name = "exchangeNos") String[] exchangeNoArray, @RequestParam String status) {
		System.out.println("===== ExchangeController exchangeUpdateToComplete() start =====");
		
		for(int i = 0; i < exchangeNoArray.length; i++) {
			System.out.println("exchangeNo : "+exchangeNoArray[i].toString());
		}
		
		exchangeService.modifyExchangeStatusByExchangeNos(exchangeNoArray, status);
		
		return "redirect:/admin";
	}
	*/
	
	
	//coupon
	@GetMapping(value = "/coupons-form")
	public String couponsForm() {
		log.info("Coupon Controller couponsForm() start");
		
		return "yjh/createCoupon";
	}
	
	@ResponseBody
	@PostMapping(value = "/coupons/coupons-no")
	public String genetateCouponNo() {
		log.info("Coupon Controller genetateCouponNo() start");
		
		return adminService.generateCouponNo();
	}
	
		
	@PostMapping(value = "/coupons") 
	public String couponAdd(@RequestParam(name = "couponValue") String couponValue, @RequestParam(name = "couponNo") String couponNo) {
		log.info("Coupon Controller couponAdd() start");
		adminService.addCoupon(couponValue, couponNo);
		
		return "redirect:/admin";
	}
	
	//company-sales
	@GetMapping(value = "/company-sales")
	public String companySales() {
		
		return "yjh/company-sales";
	}
	
	@ResponseBody
	@GetMapping(value = "/company-sales/years")
	public List<CompanySalesDto> companySalesListByYears(HttpServletRequest request) throws ParseException {
		log.info("Company-sales Controller companySalesListByYears() start");
		log.info("startDate : "+request.getParameter("startDate"));
		log.info("endDate : "+request.getParameter("endDate"));
		
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		
		CompanySalesDto companySalesDto = new CompanySalesDto();
		companySalesDto.setStartDate(startDate);
		companySalesDto.setEndDate(endDate);
		
		List<CompanySalesDto> csList = adminService.findCompanySalesByYears(companySalesDto);
		log.info("csList : "+csList.toString());
		
		return csList;
	}
	
	//talent
	@GetMapping(value = "/talents") // talents
	public String talentList(Model model) {
		log.info("Talent Controller talentList() start");
		List<Talent> talentList =  adminService.findTalent();
		model.addAttribute("talentList", talentList);
		
		return "yjh/talent";
	}
	
//	@PutMapping(value = "/talents/{sellerId}")
//	public String talentModifyByTalentNos(@PathVariable("sellerId") List<String> sellerId) {
//		System.out.println("===== TalentController talentModifyByTalentNos() start =====");
//		System.out.println("sellerId : "+sellerId.toString());		
//		
//		
//		return "";
//	}
	
}	
