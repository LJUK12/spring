package com.project.finalProject.dao;

import java.util.ArrayList;
import java.util.HashMap;

import com.project.finalProject.model.MemberVO;

public interface IMemberDAO {
	MemberVO loginCheck(HashMap<String, Object> map); // 로그인
	String memIdCheck(String memId); // id 중복 확인
	String memEmailCheck(String email); // 이메일 중복 확인
	void memJoin(MemberVO vo); // 회원가입
	
	ArrayList<MemberVO> listAllProfile();   	// 전체 정보 조회
	MemberVO profileInfo(String memId);			// 상세 상품 조회
	void updateProfile(MemberVO memVo);			// 상품 정보 수정
}
