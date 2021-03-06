package com.my.azusato.unit.login;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.login.Grant;
import com.my.azusato.login.LoginUser;
import com.my.azusato.login.UserDetailServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDetailServiceImplTest extends AbstractIntegration {

	@Autowired
	UserDetailServiceImpl userDetailServiceImpl;
	
	@Autowired
	MessageSource ms;

	final String RESOUCE_BASIC_PATH = "src/test/data/unit/login/userDetailServiceImpl/";

	@Nested
	class Login {

		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "login/";
		
		final String USER_NAME = Entity.createdVarChars[0];

		@ParameterizedTest
		@MethodSource("com.my.azusato.unit.login.UserDetailServiceImplTest#thenRealtiveUser_resultOk")
		public void thenRealtiveUser_resultOk(String folderName, Type expectedType , List<GrantedAuthority> expectedRoles) throws Exception {
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			

			LoginUser loginUser = (LoginUser) userDetailServiceImpl.loadUserByUsername(USER_NAME);
			log.debug("result : {}",loginUser);
			

			assertEquals(USER_NAME, loginUser.getUsername());
			assertEquals(Entity.createdVarChars[1],loginUser.getPassword());
			for (GrantedAuthority results : loginUser.getAuthorities()) {
				Assertions.assertTrue(expectedRoles.contains(results));
			}
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void thenDeletedUser_resultError(Locale locale) throws Exception {
			String folderName = "5";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			Assertions.assertThrows(UsernameNotFoundException.class, 
					()->userDetailServiceImpl.loadUserByUsername("NotExistUser")
			);
			
		}
		
		@Test
		public void givenNotExistData_resultUsernameNotFoundException() throws Exception {
			Assertions.assertThrows(UsernameNotFoundException.class, 
					()->userDetailServiceImpl.loadUserByUsername("NotExistUser")
			);
		}
	}
	
	public static Stream<Arguments> thenRealtiveUser_resultOk(){
		return Stream.of(
					Arguments.of("1",Type.admin,AuthorityUtils.createAuthorityList(Grant.ROLE_PRIFIX+UserEntity.Type.admin.toString())),
					Arguments.of("2",Type.kakao,AuthorityUtils.createAuthorityList(Grant.ROLE_PRIFIX+UserEntity.Type.kakao.toString())),
					Arguments.of("3",Type.line,AuthorityUtils.createAuthorityList(Grant.ROLE_PRIFIX+UserEntity.Type.line.toString())),
					Arguments.of("4",Type.nonmember,AuthorityUtils.createAuthorityList(Grant.ROLE_PRIFIX+UserEntity.Type.nonmember.toString()))
				);
	}
}
