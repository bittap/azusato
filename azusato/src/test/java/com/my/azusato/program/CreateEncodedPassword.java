package com.my.azusato.program;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateEncodedPassword {

	public static void main(String[] args) {
		String targetPassword = "xodud137";
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		String encodedPassword = encoder.encode(targetPassword);
		System.out.printf("%s -> %s",targetPassword,encodedPassword);
	}
}
