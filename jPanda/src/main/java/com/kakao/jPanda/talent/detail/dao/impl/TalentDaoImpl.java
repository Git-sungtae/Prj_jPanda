package com.kakao.jPanda.talent.detail.dao.impl;

import java.util.List;
import java.util.Map;


import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kakao.jPanda.talent.detail.dao.TalentDao;
import com.kakao.jPanda.talent.detail.domain.BambooUseDto;
import com.kakao.jPanda.talent.detail.domain.ReportDto;
import com.kakao.jPanda.talent.detail.domain.ReviewDto;
import com.kakao.jPanda.talent.detail.domain.TalentDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TalentDaoImpl implements TalentDao {
	
	private final SqlSession sqlSession;
	
	// 재능 판매 상세 페이지 불러오기
	@Override
	public TalentDto selectBoardTalentByTalentNo(Long talentNo) {
		return sqlSession.selectOne("selectBoardTalentByTalentNo", talentNo);
	}
	
	// 리뷰 리스트 불러오기
	@Override
	public List<ReviewDto> selectReivewListByTalentNo(Long talentNo) {
		return sqlSession.selectList("selectReviewListByTalentNo", talentNo);
	}
	
	// 리뷰 업데이트
	@Override
	public int updateReview(ReviewDto review) {
		return sqlSession.update("updateReview", review);
	}
	
	// 리뷰 삭제
	@Override
	public int deleteReview(ReviewDto review) {
		return sqlSession.delete("deleteReview", review);
	}

	// 재능 판매종료 (업데이트)
	@Override
	public int updateTalent(Long talentNo) {
		return sqlSession.update("updateTalent", talentNo);
	}
	
	// 리뷰 인서트
	@Override
	public int insertReview(ReviewDto review) {
		return sqlSession.insert("insertReview", review);
	}

	// 리뷰 인서트 검증용
	@Override
	public List<BambooUseDto> selectBambooUseByTalentNoAndBuyerId(Map<String, Object> talentNoAndBuyerIdMap) {
		return sqlSession.selectList("selectBambooUseByTalentNoAndBuyerId", talentNoAndBuyerIdMap);
	}

	// 재능 구매자 정보 인서트
	@Override
	public int insertBambooUse(BambooUseDto bambooUse) {
		return sqlSession.insert("insertBambooUse", bambooUse);
	}

	// 구매 여부 확인 용도
	@Override
	public int selectBuyCheckByBambooUse(BambooUseDto bambooUse) {
		return sqlSession.selectOne("selectBuyCheckByBambooUse", bambooUse);
	}
	
	// 신고 여부 검증용
	@Override
	public int selectReportCheck(ReportDto report) {
		return sqlSession.selectOne("selectReportCheck", report);
	}

	// 신고 인서트
	@Override
	public int insertReport(ReportDto report) {
		return sqlSession.insert("insertReport", report);
	}

	// 뷰 카운트 업데이트
	@Override
	public int updateTalentToViewCount(Long talentNo) {
		return sqlSession.update("updateTalentToViewCount", talentNo);
	}

	// 리뷰 중복 인서트 검증용
	@Override
	public List<ReviewDto> selectReviewInsertCheck(ReviewDto review) {
		return sqlSession.selectList("selectReviewInsertCheck", review);
	}
	
	
	
}
