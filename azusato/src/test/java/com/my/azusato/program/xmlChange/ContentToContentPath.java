package com.my.azusato.program.xmlChange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

import com.my.azusato.view.controller.common.ValueConstant;

/**
 * お祝いテーブルの"content"フィールドが修正により、"contentPath"になったため修正
 * @author Carmel
 *
 */
public class ContentToContentPath {
	
	static String TARGET_FOLDER = "src/test/data";

	public static void main(String[] args) throws IOException {
		Stream<Path> paths = Files.walk(Paths.get(TARGET_FOLDER));
		
		paths.filter(Files::isRegularFile).forEach(t -> {
			try {
				convertContentToContentPath(t);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		//convertContentToContentPath(Paths.get("src\\test\\data\\integration\\api\\controller\\addCelebration-reply\\1\\expect.xml"));
	}
	
	//src\test\data\integration\api\controller\addCelebration-reply\1\expect.xml
	
	public static void convertContentToContentPath(Path targetPath) throws IOException {
		// 
		try(BufferedReader br = IOUtils.buffer(new FileReader(targetPath.toString(),Charset.forName(ValueConstant.DEFAULT_CHARSET))); 
				){
			List<String> lines = new LinkedList<>();
			String line;
			boolean celebrationBlock = false;
			while((line = br.readLine()) != null) {
				if(line.indexOf("<celebration") != -1 && line.indexOf("<celebration_reply") == -1) {
					celebrationBlock = true;
				}else if(line.indexOf("/>") != -1) {
					celebrationBlock = false;
				}
				if(celebrationBlock && line.indexOf("content") != -1) {
					line = line.replace("content", "content_path");
				}
				lines.add(line);
			}

			try(BufferedWriter bw = IOUtils.buffer(new FileWriter(targetPath.toString(),Charset.forName(ValueConstant.DEFAULT_CHARSET),false))){
				bw.write(lines.stream().collect(Collectors.joining("\n")));
			}
		}
	}
}
