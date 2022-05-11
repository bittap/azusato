package com.my.azusato.program;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

public class FileToBase64 {

	public static void main(String[] args) throws IOException {
		// loop8回
		int loopCount = 8;
		String commonPath = "src/main/resources/default/profile";
		String targetPath = "img";
		String destinationPath = "base64";
		for (int i = 1; i <= loopCount; i++) {
			byte[] bytes = FileUtils
					.readFileToByteArray(Paths.get(commonPath, targetPath, "avatar" + i + ".png").toFile());

			String base64 = Base64.getMimeEncoder().encodeToString(bytes);

			BufferedWriter bw = new BufferedWriter(
					new FileWriter(Paths.get(commonPath, destinationPath, "avatar" + i + ".txt").toFile()));
			bw.write(base64);
		}
		// 元ファイル読み込み -> base64

		// base64でoutput name+ .txt

	}

}
