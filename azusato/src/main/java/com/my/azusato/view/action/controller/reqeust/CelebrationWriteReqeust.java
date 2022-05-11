package com.my.azusato.view.action.controller.reqeust;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CelebrationWriteReqeust {

	@NotNull
	String title;
	
	@NotNull
	String content;
	
	@NotNull
	String name;
	
	@NotNull
	String profileImageType;
	
	@NotNull
	String profileImageBase64;
}
