package com.my.azusato.unit.locale;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpServletRequest;

import com.my.azusato.common.TestConstant;
import com.my.azusato.locale.LocalePathResolver;

public class LocalPathResloverTest {

	LocalePathResolver target;
	
	@BeforeEach
	public void initalize() {
		target = new LocalePathResolver();
	}
	
	@Nested
	@DisplayName("ResolveRocale method")
	class ResolveRocale{
		
		MockHttpServletRequest mhsr;
		
		
		
		@BeforeEach
		public void initalize() {
			mhsr = new MockHttpServletRequest();
			
		}
		
		@Nested
		@DisplayName("resolve Locale by Header")
		class Header{
			
			@Test
			public void givenJpHeader_thenReturnJpLocale() {
				List<Locale> locales = List.of(Locale.JAPANESE);
				mhsr.setPreferredLocales(locales);
				Locale result = target.resolveLocale(mhsr);
				
				Assertions.assertEquals(Locale.JAPANESE, result);
			}
			
			@Test
			public void givenKoHeader_thenReturnKoLocale() {
				List<Locale> locales = List.of(Locale.KOREAN);
				mhsr.setPreferredLocales(locales);
				Locale result = target.resolveLocale(mhsr);
				
				Assertions.assertEquals(Locale.KOREAN, result);
			}
		}
		
		@Nested
		@DisplayName("resolve Locale by Path")
		class Path{
			
			
			@ParameterizedTest
			@MethodSource("com.my.azusato.unit.locale.LocalPathResloverTest#providePaths")
			public void givenPath_thenReturnLocale(String path, Locale expected) {
				mhsr.setRequestURI(path);
				Locale result = target.resolveLocale(mhsr);
				
				Assertions.assertEquals(expected, result);
			}
			
		}

	}
	
	
	
	/**
	 * method of {@code @MethodSource} should be static. 
	 * I located here not nested class. Because It can't make static in nested class
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> providePaths() {
		Locale defaultlocale = Locale.ENGLISH;
		
		return Stream.of(
					Arguments.of(TestConstant.HOST,defaultlocale),
					Arguments.of(TestConstant.HOST.concat("/ko"),Locale.KOREAN),
					Arguments.of(TestConstant.HOST.concat("/ja"),Locale.JAPANESE),
					Arguments.of(TestConstant.HOST.concat("/api"),defaultlocale)
				);
		
	}
	
	
}
