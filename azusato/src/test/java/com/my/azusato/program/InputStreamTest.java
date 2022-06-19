package com.my.azusato.program;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class InputStreamTest {

	public static void main(String[] args) throws FileNotFoundException {
		final InputStream is = new FileInputStream("/");
        final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        final BufferedReader br = new BufferedReader(isr);
        br.lines().forEach(System.out::println);
	}
}
