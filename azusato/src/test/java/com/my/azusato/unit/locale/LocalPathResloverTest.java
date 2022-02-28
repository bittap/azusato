package com.my.azusato.unit.locale;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.my.azusato.common.TestConstant;
import com.my.azusato.locale.LocaleConstant;
import com.my.azusato.locale.LocalePathAndHeaderResolver;

public class LocalPathResloverTest {

	LocalePathAndHeaderResolver target;
	
	@BeforeEach
	public void initalize() {
		target = new LocalePathAndHeaderResolver();
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
			
			private String methodName = "resolveByHeader";
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Test
			public void givenNullHeader_thenReturnNull() {
				Iterator<Locale> iterator = new ArrayList().iterator();
				Locale result = (Locale) ReflectionTestUtils.invokeMethod(target, methodName, iterator);
				Assertions.assertNull(result);
			}
			
			@Test
			public void givenJpHeader_thenReturnJpLocale() {
				Iterator<Locale> iterator = List.of(Locale.JAPANESE).iterator();
				Locale result = (Locale) ReflectionTestUtils.invokeMethod(target, methodName, iterator);
				Assertions.assertEquals(Locale.JAPANESE, result);
			}
			
			@Test
			public void givenKoHeader_thenReturnKoLocale() {
				Iterator<Locale> iterator = List.of(Locale.KOREAN).iterator();
				Locale result = (Locale) ReflectionTestUtils.invokeMethod(target, methodName, iterator);
				Assertions.assertEquals(Locale.KOREAN, result);
			}
			
			@Test
			public void givenEnHeader_thenReturnNull() {
				Iterator<Locale> iterator = List.of(Locale.ENGLISH).iterator();
				Locale result = (Locale) ReflectionTestUtils.invokeMethod(target, methodName, iterator);
				Assertions.assertNull(result);
			}
			
//			@Test
//			public void givenJpHeader_thenReturnJpLocale() {
//				List<Locale> locales = List.of(Locale.JAPANESE);
//				mhsr.setPreferredLocales(locales);
//				Locale result = target.resolveLocale(mhsr);
//				
//				Assertions.assertEquals(Locale.JAPANESE, result);
//			}
//			
//			@Test
//			public void givenKoHeader_thenReturnKoLocale() {
//				List<Locale> locales = List.of(Locale.KOREAN);
//				mhsr.setPreferredLocales(locales);
//				Locale result = target.resolveLocale(mhsr);
//				
//				Assertions.assertEquals(Locale.KOREAN, result);
//			}
		}
		
		@Nested
		@DisplayName("resolve Locale by Path")
		class Path{
			
			private String methodName = "resolveByPath";
			
			@ParameterizedTest
			@MethodSource("com.my.azusato.unit.locale.LocalPathResloverTest#providePaths")
			public void givenPath_thenReturnLocale(String path, Locale expected) {
				Locale result = ReflectionTestUtils.invokeMethod(target, methodName, path);
				
				Assertions.assertEquals(expected, result);
			}
			
			
//			@ParameterizedTest
//			@MethodSource("com.my.azusato.unit.locale.LocalPathResloverTest#providePaths")
//			public void givenPath_thenReturnLocale(String path, Locale expected) {
//				mhsr.setRequestURI(path);
//				Locale result = target.resolveLocale(mhsr);
//				
//				Assertions.assertEquals(expected, result);
//			}
			
		}
		
		@Nested
		@DisplayName("resolve Locale by path and header")
		class PathAndHeader{
			
			@ParameterizedTest
			@MethodSource("com.my.azusato.unit.locale.LocalPathResloverTest#providePathsAndHeaders")
			public void givenPath_thenReturnLocale(String path, List<Locale> headers,Locale expected) {
				mhsr.setRequestURI(path);
				if(Objects.nonNull(headers)) {
					mhsr.setPreferredLocales(headers);
				}  
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
		
		return Stream.of(
					Arguments.of(TestConstant.HOST,null),
					Arguments.of(TestConstant.HOST.concat("/ko"),Locale.KOREAN),
					Arguments.of(TestConstant.HOST.concat("/ja"),Locale.JAPANESE),
					Arguments.of(TestConstant.HOST.concat("/api"),null)
				);
		
	}
	
	/**
	 * <pre>
	 *  -------------------------
	 *  4 x 4
		Header	Path	result
		null	default	ja(default)
		jp		ko	
		ko		ja
		en		api
		----------------------
		Path	Header result
		default	null ja(default)
				jp	jp
				ko	ko
				en	ja(default)
				
		Path	Header result
		ko		null ko
				jp	ko
				ko	ko
				en	ko
				
		Path	Header result
		ja		null ja
				jp	ja
				ko	ja
				en	ja
				
		Path	Header result
		api		null ja(default)
				jp	jp
				ko	ko
				en	ja(default)	
	 * </pre>
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> providePathsAndHeaders() {
		
		return Stream.of(
					// Path : default
					Arguments.of(TestConstant.HOST,null,LocaleConstant.DEFAULT_LOCALE),
					Arguments.of(TestConstant.HOST,List.of(Locale.JAPANESE),Locale.JAPANESE),
					Arguments.of(TestConstant.HOST,List.of(Locale.KOREAN),Locale.KOREAN),
					Arguments.of(TestConstant.HOST,List.of(Locale.ENGLISH),LocaleConstant.DEFAULT_LOCALE),
					
					// Path : ko
					Arguments.of(TestConstant.HOST.concat("/ko"),null,Locale.KOREAN),
					Arguments.of(TestConstant.HOST.concat("/ko"),List.of(Locale.JAPANESE),Locale.KOREAN),
					Arguments.of(TestConstant.HOST.concat("/ko"),List.of(Locale.KOREAN),Locale.KOREAN),
					Arguments.of(TestConstant.HOST.concat("/ko"),List.of(Locale.ENGLISH),Locale.KOREAN),
					
					// Path : ja
					Arguments.of(TestConstant.HOST.concat("/ja"),null,Locale.JAPANESE),
					Arguments.of(TestConstant.HOST.concat("/ja"),List.of(Locale.JAPANESE),Locale.JAPANESE),
					Arguments.of(TestConstant.HOST.concat("/ja"),List.of(Locale.KOREAN),Locale.JAPANESE),
					Arguments.of(TestConstant.HOST.concat("/ja"),List.of(Locale.ENGLISH),Locale.JAPANESE),
					
					
					// Path : api
					Arguments.of(TestConstant.HOST.concat("/api"),null,LocaleConstant.DEFAULT_LOCALE),
					Arguments.of(TestConstant.HOST.concat("/api"),List.of(Locale.JAPANESE),Locale.JAPANESE),
					Arguments.of(TestConstant.HOST.concat("/api"),List.of(Locale.KOREAN),Locale.KOREAN),
					Arguments.of(TestConstant.HOST.concat("/api"),List.of(Locale.ENGLISH),LocaleConstant.DEFAULT_LOCALE)
				);
		
	}
	
	
}
