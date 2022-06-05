package com.my.azusato.login;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.exception.AzusatoException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Grant {
	
	private final HttpServletRequest httpServletRequest;
	
	private final MessageSource ms;
	
	/**
	 * SpringはROLE_から始まらないとエラーになる。
	 */
	public static final String ROLE_PRIFIX = "ROLE_";
	public static final String ADMIN_ROLE = ROLE_PRIFIX+UserEntity.Type.admin.toString();
	public static final String KAKAO_ROLE = ROLE_PRIFIX+UserEntity.Type.kakao.toString();
	public static final String LINE_ROLE = ROLE_PRIFIX+UserEntity.Type.line.toString();
	public static final String NONMEMBER_ROLE = ROLE_PRIFIX+UserEntity.Type.nonmember.toString();
	
	private static final List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList(ADMIN_ROLE);
	private static final List<GrantedAuthority> KAKAO_ROLES = AuthorityUtils.createAuthorityList(KAKAO_ROLE);
	private static final List<GrantedAuthority> LINE_ROLES = AuthorityUtils.createAuthorityList(LINE_ROLE);
	private static final List<GrantedAuthority> NONMEMBER_ROLES = AuthorityUtils.createAuthorityList(NONMEMBER_ROLE);
	
	/**
	 * ロールがあるかどうかチェック。
	 * {@link List#contains(Object)}を利用しチェックする。
	 * @param grants ロールリスト
	 * @param targetGrant ターゲットロール
	 * @return targetGrantがあると true , ないと false
	 */
	public boolean containGrantedAuthority(Collection<GrantedAuthority> grants, String targetGrant) {
		return grants.contains(new SimpleGrantedAuthority(targetGrant));
	}
	
	/**
	 * ロールを解決する。
	 * @param userType ユーザのロール
	 * @return userTypeによる権限
	 * @throws AzusatoException パラメータが存在しないユーザタイプ
	 */
	public List<GrantedAuthority> resolveRoles(Type userType) {
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
					ms.getMessage(AzusatoException.I0005, new String[] {}, httpServletRequest.getLocale()));
		}
    }
	
	
}
