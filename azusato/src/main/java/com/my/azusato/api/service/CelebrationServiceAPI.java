package com.my.azusato.api.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.request.ModifyCelebationServiceAPIRequest;
import com.my.azusato.api.service.response.GetCelebrationContentSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationContentSerivceAPIResponse.CelebrationReply;
import com.my.azusato.api.service.response.GetCelebrationSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.Celebration;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.entity.common.DefaultValueConstant;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.page.MyPageRequest;
import com.my.azusato.page.MyPaging;
import com.my.azusato.property.CelebrationProperty;
import com.my.azusato.repository.CelebrationNoticeRepository;
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
	
	private final CelebrationProperty celeProperty;

	private final MessageSource messageSource;

	private final CelebrationRepository celeRepo;
	
	private final CelebrationNoticeRepository celeNotiRepo;

	/**
	 * delegate
	 * {@link CelebrationServiceAPI#addCelebartion(AddCelebrationServiceAPIRequest, Locale, String)}.
	 * 
	 * @param req    request parameter.
	 * @param locale locale of client.
	 * @throws IOException 
	 */
	@MethodAnnotation(description = "?????????????????????(?????????)")
	public void addCelebartionAdmin(AddCelebrationServiceAPIRequest req, Locale locale) throws IOException {
		addCelebartion(req, locale, "admin");
	}

	/**
	 * delegate
	 * {@link CelebrationServiceAPI#addCelebartion(AddCelebrationServiceAPIRequest, Locale, String)}.
	 * 
	 * @param req    request parameter.
	 * @param locale locale of client.
	 * @throws IOException 
	 */
	@MethodAnnotation(description = "?????????????????????(???????????????)")
	public void addCelebartion(AddCelebrationServiceAPIRequest req, Locale locale) throws IOException {
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
	 * @throws IOException 
	 */
	@Transactional
	private void addCelebartion(AddCelebrationServiceAPIRequest req, Locale locale, String userType) throws IOException {
		UserEntity userEntity = userRepo.findByNoAndCommonFlagDeleteFlag(req.getUserNo(),ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(() -> {
			throw AzusatoException.createI0005Error(locale, messageSource, UserEntity.TABLE_NAME_KEY);
		});

		LocalDateTime nowLdt = LocalDateTime.now();
		
		CommonDateEntity commonDateEntity = userEntity.getCommonDate();
		commonDateEntity.setUpdateDatetime(nowLdt);
		
		userEntity.setName(req.getName());
		
		UserEntity savedUserEntity = userRepo.save(userEntity);
		
		CelebrationEntity insertedEntity = CelebrationEntity.builder().title(req.getTitle())
				.commonUser(
						CommonUserEntity.builder().createUserEntity(savedUserEntity).updateUserEntity(savedUserEntity).build())
				//.notices(admins)
				.readCount(DefaultValueConstant.READ_COUNT)
				.commonDate(CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build()).build();
		
		CelebrationEntity insetredEntity = celeRepo.save(insertedEntity);
		
		String contentFileName = uploadContent(req.getContent(), insetredEntity.getNo());
		insetredEntity.setContentPath(contentFileName);
		
		insetredEntity = celeRepo.save(insetredEntity);
		
		List<CelebrationNoticeEntity> notices = new ArrayList<>();

		if (userType.equals("not_admin")) {
			// ????????????????????????
			Set<UserEntity> admins = userRepo.findByUserTypeAndCommonFlagDeleteFlag(Type.admin.toString(),ValueConstant.DEFAULT_DELETE_FLAG);
			for (UserEntity admin : admins) {
				CelebrationNoticeEntity notice = CelebrationNoticeEntity.builder()
													.celebration(insertedEntity)
													.readed(false)
													.targetUser(admin)
													.commonDate(CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
													.commonFlag(CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build()).build();
				
				notices.add(notice);
				
				celeNotiRepo.save(notice);
			}
		}
		
		// ????????????
		//insertedEntity.setNotices(notices);
		// TODO ???????????????????????????????????????????????????????????????
		
		
	}
	
	/**
	 * ??????????????????
	 * ??????????????????????????????????????????????????????????????????????????????
	 * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ???????????????????????????????????????????????????
	 * 
	 * 
	 * @param ModifyCelebationServiceAPIRequest ????????????
	 * @param locale ???????????????????????????
	 * @throws IOException 
	 * @throws AzusatoException ?????????????????????????????????????????????????????????????????????
	 */
	@Transactional
	@MethodAnnotation(description = "?????????????????????")
	public void modifyCelebartion(ModifyCelebationServiceAPIRequest req , Locale locale) throws IOException{
		CelebrationEntity fetchedCelebationEntity = 
				celeRepo.findByNoAndCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(req.getCelebationNo(),ValueConstant.DEFAULT_DELETE_FLAG,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationEntity.TABLE_NAME_KEY);
				});
		
		// ?????????????????????????????????????????????????????????
		long createdUserNo = fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo();
		
		if(createdUserNo != req.getUserNo()) {
			String message = messageSource.getMessage(AzusatoException.I0006, null, locale);
			throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006, message);
		}
		LocalDateTime now = LocalDateTime.now();
		fetchedCelebationEntity.setTitle(req.getTitle());
		fetchedCelebationEntity.setContentPath(getContentFileName(fetchedCelebationEntity.getNo()));
		fetchedCelebationEntity.getCommonUser().getCreateUserEntity().setName(req.getName());
		fetchedCelebationEntity.getCommonDate().setUpdateDatetime(now);
		fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getCommonDate().setUpdateDatetime(now);
	
		celeRepo.save(fetchedCelebationEntity);
		
		uploadContent(req.getContent(),fetchedCelebationEntity.getNo());
	}
	
	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????
	 * ??????????????????????????????????????????????????????????????????????????????
	 * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ???????????????????????????????????????????????????
	 * 
	 * 
	 * @param celebationNo ??????????????????????????????
	 * @param userNo ?????????????????????????????????
	 * @param locale ???????????????????????????
	 * @throws AzusatoException ?????????????????????????????????????????????????????????????????????
	 */
	@Transactional
	@MethodAnnotation(description = "?????????????????????")
	public void deleteCelebartion(Long celebationNo, Long userNo,Locale locale) {
		CelebrationEntity fetchedCelebationEntity = 
				// ??????????????????????????????????????????????????????????????????????????????
				celeRepo.findByNoAndCommonFlagDeleteFlag(celebationNo,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationEntity.TABLE_NAME_KEY);
				});
		
		// ?????????????????????????????????????????????????????????
		long createdUserNo = fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo();
		
		if(createdUserNo != userNo) {
			String message = messageSource.getMessage(AzusatoException.I0006, null, locale);
			throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006, message);
		}
		
		LocalDateTime now = LocalDateTime.now();
		fetchedCelebationEntity.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);
		fetchedCelebationEntity.getCommonDate().setUpdateDatetime(now);
		
		// ????????????????????????
		fetchedCelebationEntity.getReplys().parallelStream().forEach((e)->{
			e.getCommonDate().setUpdateDatetime(now);
			e.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);	
		});
		
		// ???????????????????????????
		fetchedCelebationEntity.getNotices().parallelStream().forEach((e)->{
			e.getCommonDate().setUpdateDatetime(now);
			e.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);		
		});
	
		celeRepo.save(fetchedCelebationEntity);
	}
	

	/**
	 * ???????????????????????????????????????????????????
	 * ??????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param celebationNo ??????????????????????????????
	 * @param locale ???????????????????????????
	 * @throws AzusatoException ???????????????????????????
	 */
	@Transactional
	@MethodAnnotation(description = "??????????????????????????????????????????")
	public void readCountUp(Long celebationNo, Locale locale) {
		CelebrationEntity fetchedCelebationEntity = 
				// note??????????????????????????????????????????????????????????????????????????????????????????deleted??????????????????
				celeRepo.findById(celebationNo).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationEntity.TABLE_NAME_KEY);
				});
		
		int upedReadCount = fetchedCelebationEntity.getReadCount() + 1;
		fetchedCelebationEntity.setReadCount(upedReadCount);
	
		celeRepo.save(fetchedCelebationEntity);
	}

	/**
	 * ????????????????????????????????????
	 * @param celebationNo ????????????
	 * @param locale ???????????????????????????
	 * @return ????????????????????????
	 * @throws AzusatoException ???????????????????????????????????????
	 */
	@MethodAnnotation(description = "?????????????????????????????????")
	public GetCelebrationSerivceAPIResponse getCelebration(Long celebationNo , Locale locale) {
		CelebrationEntity fetchedCelebationEntity = celeRepo.findById(celebationNo).orElseThrow(()->{
			throw AzusatoException.createI0005Error(locale, messageSource, CelebrationEntity.TABLE_NAME_KEY);
		});
		
		return GetCelebrationSerivceAPIResponse.builder()
					.celebrationNo(fetchedCelebationEntity.getNo())
					.title(fetchedCelebationEntity.getTitle())
					.content(fetchedCelebationEntity.getContentPath())
					.name(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getName())
					.profileImagePath(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getProfile().getImagePath())
					.build();
	}
	
	/**
	 * ????????????????????????????????????
	 * @param celebationNo ????????????
	 * @param userNo ??????????????????????????????????????????????????????????????????????????????????????????????????????
	 * @param locale ???????????????????????????
	 * @return ????????????????????????
	 * @throws AzusatoException ??????????????????????????????????????????????????????
	 */
	@Transactional
	@MethodAnnotation(description = "??????????????????????????????????????????????????????????????????????????????")
	public GetCelebrationContentSerivceAPIResponse getCelebrationContent(Long celebationNo , Long userNo, Locale locale) {
		CelebrationEntity fetchedCelebationEntity = celeRepo.findById(celebationNo).orElseThrow(()->{
			throw AzusatoException.createI0005Error(locale, messageSource, CelebrationEntity.TABLE_NAME_KEY);
		});
		
		return GetCelebrationContentSerivceAPIResponse.builder()
			.contentPath(fetchedCelebationEntity.getContentPath())
			.no(fetchedCelebationEntity.getNo())
			.owner(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo() == userNo ? true : false)
			.replys(fetchedCelebationEntity.getReplys().stream()
					// deleted == false??????
					.filter((e1)->{
						return e1.getCommonFlag().getDeleteFlag() == ValueConstant.DEFAULT_DELETE_FLAG;
					})
					// No??????Orderby
					.sorted((e1,e2)->Long.compare(e1.getNo(), e2.getNo()))
					.map((e2)->{
						CelebrationReply reply = CelebrationReply.builder()
								.no(e2.getNo())
								.content(e2.getContent())
								.createdDatetime(e2.getCommonDate().getCreateDatetime())
								.owner(e2.getCommonUser().getCreateUserEntity().getNo() == userNo ? true : false)
								.name(e2.getCommonUser().getCreateUserEntity().getName())
								.profileImagePath(e2.getCommonUser().getCreateUserEntity().getProfile().getImagePath())
								.build();
						return reply;
					})
					.collect(Collectors.toList())
			)
			.build();
	}

	/**
	 * ????????????????????????????????????
	 * ?????????No????????????????????????No??????????????????
	 * @param req ?????????????????????????????????????????????
	 * @return ?????????+??????????????????????????????
	 */
	@MethodAnnotation(description = "?????????????????????????????????")
	public GetCelebrationsSerivceAPIResponse getCelebrations(GetCelebrationsSerivceAPIRequset req) {
		Page<CelebrationEntity> celebrationEntitys = getCelebrations(req.getPageReq().getCurrentPageNo()-1, req.getPageReq().getPageOfElement());
		
		GetCelebrationsSerivceAPIResponse response = new GetCelebrationsSerivceAPIResponse();
		
		// ???????????????
		response.setPage(MyPaging.of(
					MyPageRequest.of(req.getPageReq(), celebrationEntitys.getTotalElements())
				));
		
		List<Celebration> celebrations = celebrationEntitys.stream().map((e)->{
			return Celebration.builder()
					.title(e.getTitle())
					.name(e.getCommonUser().getCreateUserEntity().getName())
					.profileImagePath(e.getCommonUser().getCreateUserEntity().getProfile().getImagePath())
					.readCount(e.getReadCount())
					.no(e.getNo())
					.createdDatetime(e.getCommonDate().getCreateDatetime())
					.build();
		}).collect(Collectors.toList());
		
		response.setCelebrations(celebrations);
		
		
		return response;
	}
	
	private Page<CelebrationEntity> getCelebrations(Integer currentPageNo, Integer pageOfElement ){
		// ?????? : ??????????????????????????????page???0??????????????????
		Pageable sortedByNo = PageRequest.of(currentPageNo, pageOfElement,Sort.by(Direction.DESC,"no"));
		return celeRepo
				.findAllByCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(sortedByNo,ValueConstant.DEFAULT_DELETE_FLAG,ValueConstant.DEFAULT_DELETE_FLAG);
	}
	
	/**
	 * ????????????????????????????????????????????????
	 * @param celebrationNo ???????????????
	 * @return ???????????????
	 * @throws AzusatoException celebrationNo???????????????????????????????????????????????????
	 */
	public Integer getPage(Long celebrationNo,Locale locale) {
		List<Long> celeNos = celeRepo.findAllCelebrationNos();
		
		Integer celeNoIndex = null;
		
		for (int i = 0; i < celeNos.size(); i++) {
			if(celeNos.get(i) == celebrationNo) {
				celeNoIndex = i+1;
				break;
			}
		}
		
		if(Objects.isNull(celeNoIndex)) {
			log.warn("CelebrationRepository#findAllCelebrationNos?????????????????????????????????????????????????????????????????????????????????\n??????????????????{}????????????????????????{}????????????????????????",celebrationNo,celeNos);
			throw AzusatoException.createI0005Error(locale, messageSource, CelebrationEntity.TABLE_NAME_KEY);
		}else {
			return (int)(Math.ceil((double)celeNoIndex / celeProperty.getPageOfElement()));
		}
	}
	
	/**
	 * ??????????????????????????????????????????????????????
	 * @param content ???????????????
	 * @param celebrationNo ??????????????????
	 * @return ?????????????????????
	 * @throws IOException
	 */
	private String uploadContent(InputStream content, Long celebrationNo) throws IOException {
		final String FOLDER = celeProperty.getServerContentFolderPath();
		final String FILE_NAME = getContentFileName(celebrationNo);
		final Path filePath = Paths.get(FOLDER,FILE_NAME);
		try(content;
				FileWriter fw = new FileWriter(filePath.toString(), Charset.forName(ValueConstant.DEFAULT_CHARSET));){
			IOUtils.copy(content, fw, Charset.forName(ValueConstant.DEFAULT_CHARSET));
			return FILE_NAME;
		}
	}
	
	private String getContentFileName(Long celebrationNo) {
		return String.valueOf(celebrationNo) + "." + celeProperty.getContentExtention();
	}
}
