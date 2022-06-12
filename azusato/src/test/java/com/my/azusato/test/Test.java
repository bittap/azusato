package com.my.azusato.test;

import java.io.File;
import java.nio.file.Paths;

import org.assertj.core.util.Arrays;

public class Test {

	public void test() {
		File[] files = Paths.get("/git/azusato/azusato/test-resources").toFile().listFiles();
		
		Arrays.asList(files).forEach(System.out::println);
	}
}
