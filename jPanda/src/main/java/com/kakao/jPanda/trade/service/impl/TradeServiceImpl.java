package com.kakao.jPanda.trade.service.impl;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakao.jPanda.trade.dao.TradeDao;
import com.kakao.jPanda.trade.domain.StatDto;
import com.kakao.jPanda.trade.domain.TalentDto;
import com.kakao.jPanda.trade.domain.TradeDto;
import com.kakao.jPanda.trade.service.TradeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class TradeServiceImpl implements TradeService{
	
	private final TradeDao tradeDao;
	
	@Autowired
	public TradeServiceImpl(TradeDao tradeDao) {
		this.tradeDao = tradeDao;
	}
	
	@Override
	public List<StatDto> findStatListByMemberId(String memberId) {
		List<StatDto> statList = new ArrayList<StatDto>();
		StatDto buyStatDto = tradeDao.selectBuyStatByMemberId(memberId); 
		StatDto sellStatDto = tradeDao.selectSellStatByMemberId(memberId); 
		StatDto refundStatDto = tradeDao.selectRefundStatByMemberId(memberId);
		
		buyStatDto.setStatType("buy");
		sellStatDto.setStatType("sell");
		refundStatDto.setStatType("refund");
		
		statList.add(buyStatDto);
		statList.add(sellStatDto);
		statList.add(refundStatDto);
		
		return statList;
	}
	
	//sell, buy, refundList 정렬을 위한 DATE 통합
	private Timestamp getDateForSort(TradeDto tradeDto) {
	    if (tradeDto.getStatusDate() != null) {
	        return tradeDto.getStatusDate();
	    } else if (tradeDto.getRefundSubmitDate() != null) {
	        return tradeDto.getRefundSubmitDate();
	    } else if (tradeDto.getPurchaseDate() != null){
	        return tradeDto.getPurchaseDate();
	    } else {
	    	return tradeDto.getRegDate();
	    }
	}
	
	
	@Override
	public List<TradeDto> findTradeListByMemberId(String memberId, String listType) {
		List<TradeDto> tradeList = new ArrayList<TradeDto>();
	
		switch (listType) {
			case "sell":
				tradeList = tradeDao.selectTradeSellListByMemberId(memberId);
				//리스트 출력을 위한 listType set
				tradeList.forEach(e -> e.setListType("sell"));
				break;
			case "buy":
				tradeList = tradeDao.selectTradeBuyListByMemberId(memberId);
				//리스트 출력을 위한 listType set
				tradeList.forEach(e -> e.setListType("buy"));
				break;
				
			case "refund":
				tradeList = tradeDao.selectTradeRefundListByMemberId(memberId);
				//리스트 출력을 위한 listType set
				tradeList.forEach(e -> e.setListType("refund"));
				break;
				
			case "all":
				List<TradeDto> tempList = tradeDao.selectTradeSellListByMemberId(memberId);
				//리스트 출력을 위한 listType set
				tempList.forEach(e -> e.setListType("sell"));
				tradeList.addAll(tempList);
				
				tempList = tradeDao.selectTradeBuyListByMemberId(memberId);
				tempList.forEach(e -> e.setListType("buy"));
				tradeList.addAll(tempList);
				
				tempList = tradeDao.selectTradeRefundListByMemberId(memberId);
				tempList.forEach(e -> e.setListType("refund"));
				tradeList.addAll(tempList);
				
				//RegDate, SubmitDate, PurchaseDate 서로비교, 내림차순 정렬
				Collections.sort(tradeList, (t1, t2) -> {
					return getDateForSort(t1).compareTo(getDateForSort(t2)) * -1;
				});
				
				break;

		}
		
		log.info("findTradeListByMemberId tradeList.size() : " + tradeList.size());
		
		return tradeList;
	}

	@Override
	public int modifyTalentByTalentNo(String talentNo, TalentDto talentDto) {
		//DB에 있는 Talent 조회
		TalentDto foundTalentDto = tradeDao.selectTradeTalentByTalentNo(talentNo);
		
		//reflection을 통한 TalentDto field 값 목록
		Field[] fields = TalentDto.class.getDeclaredFields();
        for (Field field : fields) {
        	//필드 접근 허용
            field.setAccessible(true);
            
            try {
				if (field.get(talentDto) != null) {
					log.info("before : {}, after : {}",field.get(foundTalentDto)  , field.get(talentDto));
					//field.set(해당 필드값을 set 할 인스턴스, set 해줄 값)
					field.set(foundTalentDto, field.get(talentDto));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
            
            
        }
		
		return tradeDao.updateTradeTalent(foundTalentDto);
	}

	@Override
	public int removeRefundByrefundPurchaseNo(String purchaseNo) {
		return tradeDao.deleteRefundByRefundPurchaseNo(purchaseNo);
	}

	@Override
	public TalentDto findTalentByTalentNo(String talentNo) {
		return tradeDao.selectTradeTalentByTalentNo(talentNo);
	}

	@Override
	public int addExchangeByTalentNo(TalentDto talentDto) {
		int result = 0;
		
		if ( talentDto != null) {
			result = tradeDao.insertExchangeByTalentNo(talentDto);
			
		} else {
			result = 0;
		}
		
		return result;
		
	}

	@Override
	public int addRefund(String memberId, TradeDto tradeDto) {
		tradeDto.setBuyerId(memberId);
		log.info("addRefund tradeDto.getBuyerId() : " + tradeDto.getBuyerId());
		
		return tradeDao.insertTalentRefund(tradeDto);
	}

	@Override
	public int modifyExchangeStatusByTalentNo(String talentNo) {
		return tradeDao.updateExchangeStatusByTalentNo(talentNo);
	}

	@Override
	public TradeDto findExchangeByExchangeNo(String exchangeNo) {
		return tradeDao.selectExchangeByExchangeNo(exchangeNo);
	}

	@Override
	public TradeDto findRefundByRefundPurchaseNo(String refundPurchaseNo) {
		return tradeDao.selectRefundByRefundPurchaseNo(refundPurchaseNo);
	}

	@Override
	public TradeDto findTradeByTalentNo(String talentNo) {
		return tradeDao.selectTradeByTalentNo(talentNo);
	}


}//end class