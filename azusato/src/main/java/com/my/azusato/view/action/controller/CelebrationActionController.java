package com.my.azusato.view.action.controller;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.my.azusato.view.action.controller.reqeust.CelebrationWriteReqeust;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.UrlConstant;

@Controller
@RequestMapping(value = UrlConstant.CELEBRATION_CONTROLLER_REQUSET)
public class CelebrationActionController {
	
	public CelebrationActionController() {

	}
	
	@Transactional
	@PostMapping("/write.do")
	public ResponseEntity<Void> write(CelebrationWriteReqeust req, @CookieValue(value = CookieConstant.NON_MEMBER_KEY,required = false) Cookie nonmemberCookie){
		// cookieにidがあるか確認ない場合は、非ログインユーザ登録APIを行う。
		if(nonmemberCookie == null) {
			
		}
		// 
		return null;
	}

}
