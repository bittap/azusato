package com.my.azusato.login;

import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.my.azusato.exception.AzusatoException;
import com.my.azusato.interceptor.LocaleInterceptor;
import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.ValueConstant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

	private final UserRepository userRepo;
	
	private final MessageSource ms;

	/**
	 * ユーザネームが存在しないとエラーを出す。
	 * 存在する場合はそのユーザ情報をSecurityContextに保持する。
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo
				.findByIdAndCommonFlagDeleteFlag(username, ValueConstant.DEFAULT_DELETE_FLAG)
					.map(LoginUser::new)
					.orElseThrow(()->{
						throw new UsernameNotFoundException(ms.getMessage(AzusatoException.I0009, null, LocaleInterceptor.resovledLocaleWhenPreHandle));
					});
	}

}
