package com.my.azusato.common;

import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

public class TestSource {

	public static Stream<Arguments> locales() {
		return Stream.of(Arguments.of(Locale.JAPANESE), Arguments.of(Locale.KOREAN));

	}
}
