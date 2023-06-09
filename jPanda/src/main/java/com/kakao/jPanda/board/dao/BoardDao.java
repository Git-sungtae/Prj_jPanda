package com.kakao.jPanda.board.dao;

import java.util.List;

import com.kakao.jPanda.board.domain.CategoryDto;
import com.kakao.jPanda.board.domain.FiltersDto;
import com.kakao.jPanda.talent.detail.domain.TalentDto;

public interface BoardDao {
	
	// 사이드바 대분류 카테고리 리스트 불러오기
	List<CategoryDto> selectUpperCategoryList();
	
	// 중분류 카테고리 리스트 불러오기 
	List<CategoryDto> selectLowerCategoryListByUpperCategoryNo(Long upperCategoryNo);
	 
	// 재능 리스트 필터 기능
	List<TalentDto> selectTalentListByFilter(FiltersDto filters);
	

}
