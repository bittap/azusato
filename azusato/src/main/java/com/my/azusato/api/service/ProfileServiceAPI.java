package com.my.azusato.api.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.my.azusato.api.service.request.ModifyUserProfileServiceAPIRequest;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.property.ProfileProperty;
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
	
	private final ProfileProperty profileProperty;
	
	private final Random random = new Random();
	
	
	/**
	 * イメージを外部フォルダに格納する。
	 * ファイルネームは"userNo.extention"
	 * @param bytes 書き込み対象のデータ
	 * @param extention 拡張子
	 * @param userNo ファイルネーム
	 * @return 絶対パスのファイルネーム
	 * @throws IOException 書き込みエラー
	 */
	private String uploadImage(byte[] bytes, String extention , Long userNo) throws IOException {
		final String FOLDER = profileProperty.getClientImageFolderPath();
		String fileName = String.valueOf(userNo) + "." + extention;
		Path filePath = Paths.get(FOLDER,fileName);
		try(OutputStream os = new FileOutputStream(filePath.toFile())){
			os.write(bytes);
			return "/"+fileName;
		}
	}
	
	/**
	 * 既に登録した基本イメージファイルパスを返す。 
	 * 基本イメージがあるクラスパスの中にあるファイルを全部取得し、その中でランダムでファイルのイメージパスを取得する。
	 * @return クライアント側からアクセスするpath
	 */
	public String getDefaultProfilePath()  {
		File folder = Paths.get(profileProperty.getServerDefaultImageFolderPath()).toFile();
		log.debug("folder : {}",folder.getAbsolutePath());
		
		String[] files = folder.list();
		int fileCount = files.length;

		int generatedNumber = random.nextInt(fileCount) + 1;
		int resolvedFileIndex = generatedNumber-1;
		
		log.debug("fileName : {}, fileCount : {}, resolvedFileIndex = {}",Arrays.asList(files).stream().collect(Collectors.joining(",")), fileCount,resolvedFileIndex);

		return Paths.get(profileProperty.getClientDefaultImageFolderPath(),files[resolvedFileIndex]).toString();
	}

	/**
	 * イメージをアップロードし、アップロードされたイメージパスにてイメージパスをしたい更新する。
	 * @param req request parameter
	 * @param locale エラーメッセージ用。使用しない。
	 * @throws IOException イメージを外部フォルダに書き込む時のエラー
	 * @throws AzusatoException パラメータにあるuserNoより参照したユーザ情報が存在しない。
	 */
	@Transactional
	public void updateUserProfile(ModifyUserProfileServiceAPIRequest req, Locale locale) throws IOException {
		log.debug("req : {}", req);
		
		UserEntity modifyTargetUserEntity = userRepo.findByNoAndCommonFlagDeleteFlag(req.getUserNo(),ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(() -> {
			throw AzusatoException.createI0005Error(locale, messageSource, UserEntity.TABLE_NAME_KEY);
		});
		
		String uploadedPath = uploadImage(req.getProfileImageBytes(), req.getProfileImageType(), modifyTargetUserEntity.getNo());

		ProfileEntity profileEntity = modifyTargetUserEntity.getProfile();
		profileEntity.setImagePath(uploadedPath);

		CommonDateEntity commonDateEntity = modifyTargetUserEntity.getCommonDate();
		commonDateEntity.setUpdateDatetime(LocalDateTime.now());
		
		userRepo.save(modifyTargetUserEntity);
	}
}
