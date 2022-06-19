package com.my.azusato.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.my.azusato.view.controller.common.ValueConstant;

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
	
	public static String getTestSmallCelebrationContent() throws Exception  {
		return IOUtils.toString(new FileReader(TestConstant.TEST_CELEBRATION_CONTENT_SMALL_PATH,Charset.forName(ValueConstant.DEFAULT_CHARSET)));
	}
	
	public static String getTestLargeCelebrationContent() throws Exception  {
		return IOUtils.toString(new FileReader(TestConstant.TEST_CELEBRATION_CONTENT_LARGE_PATH,Charset.forName(ValueConstant.DEFAULT_CHARSET)));
	}
}
