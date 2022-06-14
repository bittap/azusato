package com.my.azusato.test;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Test {

	public void test()  {

	}
	
	public static void main(String[] args) throws JsonProcessingException {
		List<String> PERMIT_IMAGE_TYPES = List.of("png","jpeg");
		System.out.println(PERMIT_IMAGE_TYPES);
	}
}
