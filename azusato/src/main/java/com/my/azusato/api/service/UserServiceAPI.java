package com.my.azusato.api.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.my.azusato.api.service.request.AddNonMemberUserServiceAPIRequest;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.ValueConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service API for user.
 * <ul>
 * <li>add nonmember.</li>
 * </ul>
 * 
 * @author kim-t
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceAPI {

	private final UserRepository userRepo;

	/**
	 * add nonmember for user table.
	 * 
	 * @param req request parameter
	 */
	public long addNonMember(AddNonMemberUserServiceAPIRequest req) {
		log.debug("{}#addNonMember , req : {}", UserServiceAPI.class.getName(), req);

		ProfileEntity profileEntity = ProfileEntity.builder().ImageBase64(req.getProfileImageBase64())
				.ImageType(req.getProfileImageType()).build();

		LocalDateTime currentDatetime = LocalDateTime.now();

		UserEntity userEntity = UserEntity.builder().id(req.getId()).userType(Type.nonmember.toString())
				.name(req.getName()).profile(profileEntity)
				.commonDate(CommonDateEntity.builder().createDatetime(currentDatetime).updateDatetime(currentDatetime)
						.build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(ValueConstant.DEFAULT_DELETE_FLAG).build()).build();
		profileEntity.setUser(userEntity);

		UserEntity savedUserEntity = userRepo.save(userEntity);

		return savedUserEntity.getNo();
	}
}
