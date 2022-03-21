package com.my.azusato.integration.view;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class Test {

	@ParameterizedTest
	@MethodSource("com.my.azusato.integration.view.Test#testPara(String)")
	public void test(String test) {
		Assertions.assertEquals(test, "test");
	}
}
