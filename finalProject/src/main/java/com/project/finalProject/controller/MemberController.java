package com.project.finalProject.controller;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.finalProject.model.MemberVO;
import com.project.finalProject.service.MemberService;


@Controller
public class MemberController {

	// DI 설정
		@Autowired
		MemberService service;
		
		// 로그인 처리
		@ResponseBody
		@RequestMapping("/loginCheck")
		public String loginCheck(@RequestParam HashMap<String, Object> param, Model model,
												HttpSession session) {
			// 로그인 체크
			MemberVO vo = service.loginCheck(param);
			String result = "fail";
			if(vo!=null) {
				// 로그인 성공 시 세션 변수 지정
				session.setAttribute("sid", vo.getMemId());
				
				session.setAttribute("No", vo.getMemNo());
				System.out.println(vo.getMemNo());
				result ="success";
			}
			return result;
		}
		
		// 로그아웃
		@RequestMapping("/logout")
		public String logout(HttpSession session) {
			session.invalidate();
			return "redirect:/";
		}
		
		// 사용자 아이디 중복 확인
		@ResponseBody
		@RequestMapping("/memIdCheck")
		public String memIdCheck(@RequestParam("memId") String memId) {
			String memId_result = service.memIdCheck(memId);
			
			String result = "use";
			if(memId_result != null)
				result = "no_use";
			
			return result;
		}
		
		// 사용자 이메일 중복 확인
		@ResponseBody
		@RequestMapping("/memEmailCheck")
		public String memEmailCheck(@RequestParam("memEmailId") String memEmailId, @RequestParam("memEmail") String memEmail) {
			String email = memEmailId + '@' + memEmail;
			String memEmail_result = service.memEmailCheck(email);
			
			String result = "use";
			if(memEmail_result != null)
				result = "no_use";
			
			return result;
		}
		
		// 회원가입
		@RequestMapping("/memJoin")
		public String memJoin(MemberVO vo) {
			service.memJoin(vo);
			
			return "redirect:/";
		}
		
		// 전체 정보 조회
		@RequestMapping("/listAllProfile")
		public String listAllProfile() { //Model model
//			ArrayList<MemberVO> infoList = service.listAllProfile();
//			model.addAttribute("infoList", infoList);		
			return "profileListView";
		}
		
		// 프로필 수정 폼으로 이동
		@RequestMapping("/updateProfileForm")///{memId}
		public String updateProfileForm() {
			// @PathVariable String memId, Model model
//			// 상품번호 전달하고, 해당 상품 정보 받아오기 
//			MemberVO mem = service.profileInfo(memId); // 상세 상품 조회 메소드 그대로 사용
//			model.addAttribute("mem", mem);
			return "updateProfileForm";
		}
		
		// 상품 정보 수정 : 수정된 상품 정보 DB에 저장
		@RequestMapping("/updateProfile")
		public String updateProfile() { //MemberVO mem
//			service.updateProfile(mem);		
			return "redirect:./listAllProfile";
		}
}
