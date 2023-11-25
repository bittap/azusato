package com.my.azusato.integration.locale;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.common.TestConstant;
import com.my.azusato.integration.AbstractIntegrationForTest;
import com.my.azusato.locale.LocaleConstant;

public class LocalPathResloverTest extends AbstractIntegrationForTest {

	private final static String URL = "/";
	
	private final static String HEADER_TARGET_STRING = "Content-Language";
	
	private final static String KO_URL = URL + "ko/";
	
	private final static String JA_URL = URL + "ja/";
	
	
	
	@Nested
	class Header{
		
		@Test
		public void givenDefaultPath_returnDefault() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders
						.get(URL)
						.locale(Locale.ENGLISH))
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_JA_STR));
		}
		
		@Test
		public void givenKoHeader_returnKo() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders
						.get(URL)
						.locale(Locale.KOREAN))
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_KO_STR));
		}
		
		@Test
		public void givenJpHeader_returnJp() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders
						.get(URL)
						.locale(Locale.JAPANESE))
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_JA_STR));
		}
	}
	
	@Nested
	class Path{
		
		@Test
		public void givenDefaultPath_returnKo() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders
						.get(URL))
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_JA_STR));
		}
		
		@Test
		public void givenKoPath_returnKo() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders
						.get(KO_URL))
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_KO_STR));
		}
		
		@Test
		public void givenJpPath_returnJp() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders
						.get(JA_URL))
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_JA_STR));
		}
	}
	
	/**
	 * mix path with header test. Expect result is path than Header
	 */
	@ParameterizedTest
	@MethodSource("pathAndHeader")
	public void pathAndHeader(String url, Locale locale, ResultMatcher expected) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get(url)
				.locale(locale))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(expected);
	}
	
	private static Stream<Arguments> pathAndHeader() {
		return Stream.of(
					// url : locale : expcetResult
				
					// default : default : default
					Arguments.of(URL,LocaleConstant.DEFAULT_LOCALE, header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_JA_STR)),
					// default : ko : ko
					Arguments.of(URL,Locale.KOREAN, header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_KO_STR)),
					// default : ja : ja
					Arguments.of(URL,Locale.JAPANESE, header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_JA_STR)),
					
					// ko : default : ko
					Arguments.of(KO_URL,LocaleConstant.DEFAULT_LOCALE,header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_KO_STR)),
					// ko : ko : ko
					Arguments.of(KO_URL,Locale.KOREAN,header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_KO_STR)),
					// ko : ja : ko
					Arguments.of(KO_URL,Locale.JAPANESE,header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_KO_STR)),
					
					// ja : default : ja
					Arguments.of(JA_URL,LocaleConstant.DEFAULT_LOCALE,header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_JA_STR)),
					// ja : ko : ja
					Arguments.of(JA_URL,Locale.KOREAN,header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_JA_STR)),
					// ja : ja : ja
					Arguments.of(JA_URL,Locale.JAPANESE,header().string(HEADER_TARGET_STRING, TestConstant.LOCALE_JA_STR))
				);
		
	}
	
	
	
	
	
	
	
	
	
}
