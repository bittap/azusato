package com.my.azusato.api.service;

import java.time.LocalDateTime;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Service;

import com.my.azusato.api.service.request.AddNonMemberUserServiceAPIRequest;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.property.UserProperty;
import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.CookieConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceAPI {

	private final UserRepository userRepo;

	private final UserProperty userProperty;

	/**
	 * add nonmember for user table.and add cookie for id.
	 * 
	 * @param req
	 */
	public void addNonMember(AddNonMemberUserServiceAPIRequest req) {
		log.debug("{}#addNonMember START, req : {}", UserServiceAPI.class.getName(), req);

		ProfileEntity profileEntity = ProfileEntity.builder().ImageBase64(req.getProfileImageBase64())
				.ImageType(req.getProfileImageType()).build();

		LocalDateTime currentDatetime = LocalDateTime.now();

		UserEntity userEntity = UserEntity.builder().id(req.getId()).userType(Type.nonmember.toString())
				.profile(profileEntity).commonDate(CommonDateEntity.builder().createDatetime(currentDatetime)
						.updateDatetime(currentDatetime).build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(false).build()).build();

		UserEntity savedUserEntity = userRepo.save(userEntity);

		Cookie cookie = new Cookie(CookieConstant.NON_MEMBER_KEY, savedUserEntity.getId());
		cookie.setMaxAge(userProperty.getNonMemberCookieMaxTime());

		log.debug("{}#addNonMember END, cookie {} : {}", UserServiceAPI.class.getName(), cookie.getName(),
				cookie.getValue());
	}
}
