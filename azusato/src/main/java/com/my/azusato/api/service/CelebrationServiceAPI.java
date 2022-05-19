package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.Celebration;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.CelebrationReply;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.entity.common.DefaultValueConstant;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.page.MyPageRequest;
import com.my.azusato.page.MyPaging;
import com.my.azusato.repository.CelebrationRepository;
import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.ValueConstant;

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
		
		ProfileEntity profileEntity = userEntity.getProfile();
		profileEntity.setImageBase64(req.getProfileImageBase64());
		profileEntity.setImageType(req.getProfileImageType());

		CommonDateEntity commonDateEntity = userEntity.getCommonDate();
		commonDateEntity.setUpdateDatetime(nowLdt);
		
		userEntity.setName(req.getName());

		CelebrationEntity insertedEntity = CelebrationEntity.builder().title(req.getTitle()).content(req.getContent())
				.commonUser(
						CommonUserEntity.builder().createUserEntity(userEntity).updateUserEntity(userEntity).build())
				.notices(admins).readCount(DefaultValueConstant.READ_COUNT)
				.commonDate(CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build()).build();

		celeRepo.save(insertedEntity);

		log.debug("{}#addCelebartion END");
	}

	/**
	 * お祝いリストを返却する。
	 * お祝いNoの降順、書き込みNoの昇順で取得
	 * @param req お祝いリストに関するリクエスト
	 * @return お祝い+お祝い書き込みリスト
	 */
	public GetCelebrationsSerivceAPIResponse getCelebrations(GetCelebrationsSerivceAPIRequset req) {
		// 注意 : 一番最初のパラメータpageは0から始まる。
		Pageable sortedByNo = PageRequest.of(req.getPageReq().getCurrentPageNo()-1, req.getPageReq().getPageOfElement(),Sort.by(Direction.DESC,"no"));
		Page<CelebrationEntity> celebrationEntitys = celeRepo
				.findAllByCommonFlagDeleteFlag(sortedByNo,ValueConstant.DEFAULT_DELETE_FLAG);
		
		GetCelebrationsSerivceAPIResponse response = new GetCelebrationsSerivceAPIResponse();
		
		// ページング
		response.setPage(MyPaging.of(
					MyPageRequest.of(req.getPageReq(), celebrationEntitys.getTotalElements())
				));
		
		List<Celebration> celebrations = celebrationEntitys.stream().map((e)->{
			return Celebration.builder()
					.title(e.getTitle())
					.content(e.getContent())
					.name(e.getCommonUser().getCreateUserEntity().getName())
					.profileImageType(e.getCommonUser().getCreateUserEntity().getProfile().getImageType())
					.profileImageBase64(e.getCommonUser().getCreateUserEntity().getProfile().getImageBase64())
					.no(e.getNo())
					.owner(e.getCommonUser().getCreateUserEntity().getNo() == req.getLoginUserNo() ? true : false)
					.createdDatetime(e.getCommonDate().getCreateDatetime())
					.replys(e.getReplys().stream()
							// deleted == falseだけ
							.filter((e1)->{
								return e1.getCommonFlag().getDeleteFlag() == ValueConstant.DEFAULT_DELETE_FLAG;
							})
							// No昇順Orderby
							.sorted((e1,e2)->Long.compare(e1.getNo(), e2.getNo()))
							.map((e2)->{
								CelebrationReply reply = CelebrationReply.builder()
										.no(e2.getNo())
										.content(e2.getContent())
										.createdDatetime(e2.getCommonDate().getCreateDatetime())
										.owner(e2.getCommonUser().getCreateUserEntity().getNo() == req.getLoginUserNo() ? true : false)
										.name(e2.getCommonUser().getCreateUserEntity().getName())
										.profileImageType(e2.getCommonUser().getCreateUserEntity().getProfile().getImageType())
										.profileImageBase64(e2.getCommonUser().getCreateUserEntity().getProfile().getImageBase64())
										.build();
								return reply;
					}).collect(Collectors.toList()))
					.build();
		}).collect(Collectors.toList());
		
		response.setCelebrations(celebrations);
		
		
		return response;
	}
}
