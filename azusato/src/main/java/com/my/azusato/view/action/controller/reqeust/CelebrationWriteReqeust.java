package com.my.azusato.view.action.controller.reqeust;

import org.springframework.lang.NonNull;

import lombok.Data;

@Data
public class CelebrationWriteReqeust {

	@NonNull
	String title;
	
	@NonNull
	String content;
	
	@NonNull
	String name;
	
	@NonNull
	String profileImageType;
	
	@NonNull
	String profileImageBase64;
}
