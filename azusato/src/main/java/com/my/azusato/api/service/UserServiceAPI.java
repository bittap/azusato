package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.Locale;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.service.request.AddNonMemberUserServiceAPIRequest;
import com.my.azusato.api.service.response.GetSessionUserServiceAPIResponse;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.exception.AzusatoException;
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

	private final MessageSource messageSource;

	/**
	 * add nonmember for user table.
	 * 
	 * @param req request parameter
	 */
	@Transactional
	@MethodAnnotation(description = "非会員ユーザDB登録")
	public String addNonMember(AddNonMemberUserServiceAPIRequest req) {
		log.debug("{}#addNonMember , req : {}", UserServiceAPI.class.getName(), req);

		ProfileEntity profileEntity = ProfileEntity.builder().build();

		LocalDateTime currentDatetime = LocalDateTime.now();

		UserEntity userEntity = UserEntity.builder().id(req.getId()).userType(Type.nonmember.toString())
				.name(req.getName()).profile(profileEntity)
				.commonDate(CommonDateEntity.builder().createDatetime(currentDatetime).updateDatetime(currentDatetime)
						.build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(ValueConstant.DEFAULT_DELETE_FLAG).build()).build();
		profileEntity.setUser(userEntity);

		UserEntity savedUserEntity = userRepo.save(userEntity);

		return savedUserEntity.getId();
	}

	/**
	 * ユーザを情報を返す。もし、ない場合はエラーをスロー
	 * 
	 * @param no 該当するユーザ
	 * @return ユーザ情報
	 * @throws AzusatoException 該当するユーザ情報がない場合
	 */
	@MethodAnnotation(description = "ユーザ情報の返却")
	public GetSessionUserServiceAPIResponse getSessionUser(Long no, Locale locale) {
		UserEntity userEntity = userRepo.findByNoAndCommonFlagDeleteFlag(no,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(() -> {
			String tableName = messageSource.getMessage(UserEntity.TABLE_NAME_KEY, null, locale);
			log.error("not exist {} table, no : {}", tableName, no);
			throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
					messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));
		});

		GetSessionUserServiceAPIResponse response = GetSessionUserServiceAPIResponse.builder().id(userEntity.getId())
				.name(userEntity.getName()).profileImagePath(userEntity.getProfile().getImagePath()).build();

		return response;
	}
}
