package com.kakao.jPanda.trade.domain;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TradeDto {
	//talent
	private String talentNo;
	private String sellerId;
	private String upperCategoryNo;
	private String lowerCategoryNo;
	private String mainImg;
	private String title;
	private String content;
	private Long bamboo;
	private Long saleBamboo;
	private String summary;
	private String talentStatus;
	private Long viewCount;
	private Timestamp regDate;
	private Timestamp statusDate;
	
	//member
	private String memberId;
	private String password;
	private String name;
	private String tel;
	private String email;
	private String birth;
	private String gender;
	private String memberStatus;
	private String memberGrade;
	
	//talent_refund
	private String refundPurchaseNo; //purchaseNo
	private String refundStatus;
	private String issue;
	private Timestamp refundSubmitDate; //submitDate
	private Timestamp refundApproveDate; //approveDate
	private Long refundBamboo;
	
	//member_grade
	private String grade;
	private Long gradeRatio; //exchangeRatio
	
	//exchange
	private String exchangeNo;
	private String exchangeId; //sellerId
	private String exchangeTalentNo; //talentNo
	private Long sales;
	private Timestamp exchangeSubmitDate; //submitDate
	private Timestamp exchangeApproveDate; //approveDate
	private Long total;
	private String exchangeGrade; //member_grade
	private Long exchangeRatio; 
	private String exchangeStatus; 
	private Long paymentAmount;
	
	//bamboo_use
	private String bambooUsePurchaseNo; //purchaseNo
	private String buyerId;
	private String bambooUsetalentNo; //talentNo
	private Timestamp purchaseDate;
	private String useBamboo;
	
	//ExtraValues
		//TradeMapper.xml selectTradeListByParaMap
	private String listType;
		//TradeMapper.xml selectTradeListByParaMap
	private Long sellCount;
		//TradeMapper.xml selectTradeListByParaMap
	private String refundableDate;
		//payload parse
	private Timestamp approveDate;
	
}
