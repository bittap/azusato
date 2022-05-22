package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.Locale;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.azusato.api.service.request.ModifyUserProfileServiceAPIRequest;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.common.CommonDateEntity;
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
public class ProfileServiceAPI {

	private final UserRepository userRepo;

	private final MessageSource messageSource;
	

	/**
	 * update profile with name for user table.
	 * 
	 * @param req request parameter
	 * @param locale   locale of client. for error message
	 */
	@Transactional
	public void updateUserProfile(ModifyUserProfileServiceAPIRequest req, Locale locale) {
		log.debug("req : {}", req);
		
		UserEntity updateTargetEntity = userRepo.findByNoAndCommonFlagDeleteFlag(req.getUserNo(),ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(()->{
				String tableName = messageSource.getMessage(UserEntity.TABLE_NAME_KEY, null, locale);
				log.error("not exist {} table, no : {}", tableName, req.getUserNo());
				throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
						messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));
		});
		
		ProfileEntity profileEntity = updateTargetEntity.getProfile();
		profileEntity.setImageBase64(req.getProfileImageBase64());
		profileEntity.setImageType(req.getProfileImageType());

		CommonDateEntity commonDateEntity = updateTargetEntity.getCommonDate();
		commonDateEntity.setUpdateDatetime(LocalDateTime.now());
		
		updateTargetEntity.setName(req.getName());
		
		userRepo.save(updateTargetEntity);
	}
}
