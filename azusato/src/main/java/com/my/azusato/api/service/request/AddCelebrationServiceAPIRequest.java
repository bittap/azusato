package com.my.azusato.api.service.request;

import com.my.azusato.api.controller.CelebrationControllerAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * parameter of
 * {@link CelebrationControllerAPI#addCelebartion(com.my.azusato.api.controller.request.AddNonMemberUserAPIRequest, javax.servlet.http.Cookie, javax.servlet.http.HttpServletRequest)}
 * 
 * @author kim-t
 *
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AddCelebrationServiceAPIRequest {

	String title;

	String content;

	long userNo;
}
