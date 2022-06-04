package com.my.azusato.login;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.ValueConstant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

	private final UserRepository userRepo;
	
	/**
	 * ユーザネームが存在しないとエラーを出す。
	 * 存在する場合はそのユーザ情報をSecurityContextに保持する。
	 * @throws UsernameNotFoundException IDが存在しない場合。具体的なステータスコードとメッセージは{@link SecurityConfig}を参考
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo
				.findByIdAndCommonFlagDeleteFlag(username, ValueConstant.DEFAULT_DELETE_FLAG)
					.map(LoginUser::new)
					.orElseThrow(()->{
						throw new UsernameNotFoundException(String.format("username : 「%s」は存在しない、もしくは削除フラグがtrue", username));
					});
	}

}
