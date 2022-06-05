package com.my.azusato.login;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;

public class Grant {

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
	public static boolean containGrantedAuthority(Collection<GrantedAuthority> grants, String targetGrant) {
		return grants.contains(new SimpleGrantedAuthority(targetGrant));
	}
	
	/**
	 * ロールを解決する。
	 * @param userType ユーザのロール
	 * @return userTypeによる権限
	 * @throws UnsupportedOperationException サポートしないユーザタイプ
	 */
	public static List<GrantedAuthority> resolveRoles(Type userType) {
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
			throw new UnsupportedOperationException(String.format("%sはサポートしません。", userType.toString()));
		}
    }
	
	
}
