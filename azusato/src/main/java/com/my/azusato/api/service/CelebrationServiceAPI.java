package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.entity.common.DefaultValueConstant;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.repository.CelebrationRepository;
import com.my.azusato.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service API for celebration.
 * <ul>
 * <li>add a celebration.</li>
 * </ul>
 * 
 * @author kim-t
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CelebrationServiceAPI {

	private final UserRepository userRepo;

	private final MessageSource messageSource;

	private final CelebrationRepository celeRepo;

	/**
	 * delegate
	 * {@link CelebrationServiceAPI#addCelebartion(AddCelebrationServiceAPIRequest, Locale, String)}.
	 * 
	 * @param req    request parameter.
	 * @param locale locale of client.
	 */
	public void addCelebartionAdmin(AddCelebrationServiceAPIRequest req, Locale locale) {
		addCelebartion(req, locale, "admin");
	}

	/**
	 * delegate
	 * {@link CelebrationServiceAPI#addCelebartion(AddCelebrationServiceAPIRequest, Locale, String)}.
	 * 
	 * @param req    request parameter.
	 * @param locale locale of client.
	 */
	public void addCelebartion(AddCelebrationServiceAPIRequest req, Locale locale) {
		addCelebartion(req, locale, "not_admin");
	}

	/**
	 * add a celebration for celebration table. if userType is not admin. add notice
	 * data for noticing to admin.
	 * 
	 * 
	 * @param req      request parameter.
	 * @param locale   locale of client. for error message
	 * @param userType admin,etc
	 */
	@Transactional
	private void addCelebartion(AddCelebrationServiceAPIRequest req, Locale locale, String userType) {
		log.debug("{}#addCelebartion , req : {}, locale : {}", CelebrationServiceAPI.class.getName(), req, locale);

		UserEntity userEntity = userRepo.findById(req.getUserNo()).orElseThrow(() -> {
			String tableName = messageSource.getMessage(UserEntity.TABLE_NAME_KEY, null, locale);
			log.error("not exist {} table, no : {}", tableName, req.getUserNo());
			throw new AzusatoException(HttpStatus.INTERNAL_SERVER_ERROR, AzusatoException.E0001,
					messageSource.getMessage(AzusatoException.E0001, new String[] { tableName }, locale));
		});

		Set<UserEntity> admins = null;

		if (userType.equals("not_admin")) {
			// for create celebration_notice table
			// Select that user type is admin.
			admins = userRepo.findByUserType(Type.admin.toString());
		}

		LocalDateTime nowLdt = LocalDateTime.now();

		CelebrationEntity insertedEntity = CelebrationEntity.builder().title(req.getTitle()).content(req.getContent())
				.commonUser(
						CommonUserEntity.builder().createUserEntity(userEntity).updateUserEntity(userEntity).build())
				.notices(admins).readCount(DefaultValueConstant.READ_COUNT)
				.commonDate(CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build()).build();

		celeRepo.save(insertedEntity);

		log.debug("{}#addCelebartion complete, req : {}, locale : {}", CelebrationServiceAPI.class.getName(), req,
				locale);
	}
}
