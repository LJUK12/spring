package com.project.finalProject.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.project.finalProject.model.ChatVO;
import com.project.finalProject.model.PhotoVo;
import com.project.finalProject.model.PostVO;
import com.project.finalProject.service.ChatService;
import com.project.finalProject.service.ObjectDetectionService;
import com.project.finalProject.service.PostService;
import com.project.finalProject.service.TranslationService;

@Controller
public class PostController {
	
	@Autowired
	PostService postService;
	
	@Autowired
	ChatService chatService;
	
	@Autowired
	private ObjectDetectionService objService;
	
	@Autowired
	private TranslationService tsService;
	
	@RequestMapping("/insertPostForm")
	public String insertPostForm() {
		return "/post/insertPostForm";
	}
	
	@RequestMapping("/post/insertPost")
	public String insertPost(PostVO vo ) {
		
		System.out.println("content = " + vo.getPostContent());

		//
		int startIdx = vo.getPostContent().indexOf("/photo_upload/");
		
		if(startIdx != -1) {
		System.out.println("startIdx = " + startIdx);

		// +14 start

		int endIdx = vo.getPostContent().indexOf(".jpg");
		System.out.println("endIdx = " + endIdx);

		// + 4 end

		String thumbFileName = vo.getPostContent().substring((startIdx + 14), (endIdx + 4));
		System.out.println("thumbFileName = " + thumbFileName);
		

		// 포스트 썸네일 set
		vo.setPostImg(thumbFileName);
		}
		//포스트 내용의 \n을 <br>로 변경
		vo.setPostContent(vo.getPostContent().replace("\n", "<br>"));
		postService.insertPost(vo);
		return "redirect:/";
	}

	
	// 게시글 작성 - 이미지
	
	@RequestMapping("/photoUpload")
	public String photoUpload(HttpServletRequest request, PhotoVo vo){
	    String callback = vo.getCallback();
	    String callback_func = vo.getCallback_func();
	    String file_result = "";
	    try {
	        if(vo.getFiledata() != null && vo.getFiledata().getOriginalFilename() != null && !vo.getFiledata().getOriginalFilename().equals("")){

	            String original_name = vo.getFiledata().getOriginalFilename();
	            String ext = original_name.substring(original_name.lastIndexOf(".")+1);
	            String defaultPath = request.getSession().getServletContext().getRealPath("/");
	            String path = defaultPath + "resource" + File.separator + "photo_upload" + File.separator;             
	            File file = new File(path);
	            System.out.println("path:"+path);
	            if(!file.exists()) {
	                file.mkdirs();
	            }
	            String realname = UUID.randomUUID().toString() + "." + ext;
	            vo.getFiledata().transferTo(new File(path+realname));
	            file_result += "&bNewLine=true&sFileName="+original_name+"&sFileURL=/resource/photo_upload/"+realname;
	        } else {
	            file_result += "&errstr=error";
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "redirect:" + callback + "?callback_func="+callback_func+file_result;
	}
	
	
	
	//detailViewPost 상세 보기에서 사용
	
	@RequestMapping("/post/detailViewPost/{postNo}")
	public String detailViewPost(ChatVO vo,@PathVariable int postNo, Model model) {
		
		PostVO post = postService.detailVeiwPost(postNo);
		model.addAttribute("post", post);
		
		/* chatService.insertChat(vo); */
		
		ArrayList<ChatVO> chatList = chatService.listAllChat(postNo);
		model.addAttribute("chatList",chatList);
		
		ArrayList<PostVO> postList2 = postService.listAllPost();
		model.addAttribute("postList2", postList2);
		return "post/detailViewPost";
	}
	
	@RequestMapping("/searchPost")
	public String searchPost(@RequestParam("searchBarInput") String searchBarInput, Model model) {
		
		ArrayList<PostVO> postVO = postService.searchPost(searchBarInput);
		model.addAttribute("postVO", postVO);
		model.addAttribute("resultStr", searchBarInput);
		
		return "post/searchPost";
	}
	
	@RequestMapping("/objectDetection")
	public String objectDetection(@RequestParam("uploadFile") MultipartFile file, Model model) {
		ArrayList<String> strList = null; 
		System.out.println("objectDetection �넻怨�");
		String resultStr = "";
		try {
			// 1. �뙆�씪 ���옣 寃쎈줈 �꽕�젙 : �떎�젣 �꽌鍮꾩뒪�릺�뒗 �쐞移� (�봽濡쒖젥�듃 �쇅遺��뿉 ���옣)
			String uploadPath = "D:/ai/";
			
			// 2. �썝蹂� �뙆�씪 �씠由� �븣�븘�삤湲�
			String originalFileName = file.getOriginalFilename();
			String filePathName = uploadPath + originalFileName;
			
			// 3. �뙆�씪 �깮�꽦
			File file1 = new File(filePathName);
			
			// 4. �꽌踰꾨줈 �쟾�넚
			file.transferTo(file1);				
			
			// �꽌鍮꾩뒪�뿉 �뙆�씪 path�� �뙆�씪紐� �쟾�떖  -> �꽌鍮꾩뒪 硫붿냼�뱶�뿉�꽌 蹂�寃�
			// �꽌鍮꾩뒪�뿉�꽌 諛섑솚�맂 PoseVO 由ъ뒪�듃 ���옣
			strList = objService.objectDetect(filePathName);
			
			/*
			 * for(int i = 0; i<strList.size(); i++) { resultStr += strList.get(i) + " "; }
			 */
			resultStr = strList.get(0);
			System.out.println("Controller 寃곌낵 : " +resultStr);
			resultStr = tsService.papagoTranslate(resultStr);
			System.out.println("Controller papago 寃곌낵 : " +resultStr);
			ArrayList<PostVO> postVO = postService.searchPost(resultStr);
			
			model.addAttribute("resultStr", resultStr);
			model.addAttribute("postVO", postVO);
			
			
			
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "post/searchPost";
	}
	
}