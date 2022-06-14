package com.my.azusato.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class TestStream {

	public static byte[] getTestImageBytes() throws Exception  {
		try(InputStream is = new FileInputStream(new File(TestConstant.TEST_IMAGE_PATH)); BufferedInputStream bis = new BufferedInputStream(is)){
			return bis.readAllBytes();
		}
	}
	
	public static byte[] getTestLargeImageBytes() throws Exception  {
		try(InputStream is = new FileInputStream(new File(TestConstant.TEST_LARGE_IMAGE_PATH)); BufferedInputStream bis = new BufferedInputStream(is)){
			return bis.readAllBytes();
		}
	}
}
