package com.my.azusato.view.action.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.my.azusato.api.controller.UserControllerAPI;
import com.my.azusato.api.controller.request.AddNonMemberUserAPIRequest;
import com.my.azusato.util.SessionUtil;
import com.my.azusato.view.action.controller.reqeust.CelebrationWriteReqeust;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.UrlConstant;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = UrlConstant.CELEBRATION_CONTROLLER_REQUSET)
@RequiredArgsConstructor
public class CelebrationActionController {
	
	private final HttpServletRequest httpServletRequest;
	
	private final UserControllerAPI userControllerAPI;
	
	@Transactional
	@GetMapping("/write.do")
	public ResponseEntity<Void> write(CelebrationWriteReqeust req, @CookieValue(value = CookieConstant.NON_MEMBER_KEY,required = false) Cookie nonmemberCookie){
		
		// セッションが存在しない場合は、非ログインユーザ登録APIを行う。
		if(!SessionUtil.isLoginSession(httpServletRequest.getSession())) {
			AddNonMemberUserAPIRequest nonmemberReq = AddNonMemberUserAPIRequest.builder()
//						.name(req.getName())
//						.profileImageBase64(req.getProfileImageBase64())
//						.profileImageType(req.getProfileImageType())
						.build();
			userControllerAPI.addNonMember(nonmemberReq, nonmemberCookie);
		// セッションが存在
		}else {
			
		}
		
		return null;
	}

}
