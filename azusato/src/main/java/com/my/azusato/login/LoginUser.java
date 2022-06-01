package com.my.azusato.login;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.interceptor.LocaleInterceptor;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LoginUser extends User 
	implements ApplicationContextAware //Spring Beanではない場合 MessageSourceの取得のため
	{
	
	private static final long serialVersionUID = 1L;

	/**
	 * accountNonExpired,credentialsNonExpired等使用しないのは"true"
	 */
	private static final boolean NOT_USED_FIELD = true;
	
	private static ApplicationContext ctx;
	
	private static MessageSource ms;
	
	private UserEntity user;

	public LoginUser(UserEntity user) {
		super(user.getId(), user.getPassword(), user.getCommonFlag().getDeleteFlag(), 
				NOT_USED_FIELD, NOT_USED_FIELD, NOT_USED_FIELD, resolveRoles(Type.valueOf(user.getUserType())));
		
		user = this.user;
		ms = ctx.getBean(MessageSource.class);
	}
	
	/**
	 * SpringはROLE_から始まらないとエラーになる。
	 */
	private static final String ROLE_PRIFIX = "ROLE_";
	private static final List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList(ROLE_PRIFIX+UserEntity.Type.admin.toString());
	private static final List<GrantedAuthority> KAKAO_ROLES = AuthorityUtils.createAuthorityList(ROLE_PRIFIX+UserEntity.Type.kakao.toString());
	private static final List<GrantedAuthority> LINE_ROLES = AuthorityUtils.createAuthorityList(ROLE_PRIFIX+UserEntity.Type.line.toString());
	private static final List<GrantedAuthority> NONMEMBER_ROLES = AuthorityUtils.createAuthorityList(ROLE_PRIFIX+UserEntity.Type.nonmember.toString());
	
	/**
	 * ロールを解決する。
	 * @param userType ユーザのロール
	 * @return userTypeによる権限
	 * @throws AzusatoException パラメータが存在しないユーザタイプ
	 */
	private static List<GrantedAuthority> resolveRoles(Type userType) {
		switch (userType) {
		case admin:
			return ADMIN_ROLES;
		case kakao:
			return KAKAO_ROLES;
		case line:
			return LINE_ROLES;
		case nonmember:
			return NONMEMBER_ROLES;
		default:
			throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.W0001,
					ms.getMessage(AzusatoException.I0005, new String[] {}, LocaleInterceptor.resovledLocaleWhenPreHandle));
		}
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}
}
