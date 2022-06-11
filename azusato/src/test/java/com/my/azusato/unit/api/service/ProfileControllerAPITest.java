package com.my.azusato.unit.api.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.my.azusato.api.controller.ProfileControllerAPI;

@ExtendWith(MockitoExtension.class)
public class ProfileControllerAPITest {

	
	@Nested
	class GetDefaultProfilePath {
		
		@InjectMocks
		ProfileControllerAPI targetController;
		

		/**
		 * およそ10000回を実行し、もし引っかからないものがあればfailにする。
		 * 
		 * @throws Exception
		 */
		@Test
		public void getDefaultProfileBase64_normal_case() throws Exception {
			Map<String, Boolean> expect = expect();
			System.out.printf("expect : %s\n",expect);
			for (int i = 0; i < 10000; i++) {
				String result = (String) ReflectionTestUtils.invokeMethod(targetController,
						"getDefaultProfilePath");
				
				
				if(expect.containsKey(result)) {
					expect.put(result, true);
				}
			}

			expect.forEach((k,v)->{
				Assertions.assertTrue(v);
			});
		}
		
	}
	
	public static Map<String, Boolean> expect(){
		Path path = Paths.get("src","main","resources","static","image","default","profile");
		System.out.printf("absolutPath: %s\n",path.toAbsolutePath());
		List<String> fileNames = Arrays.asList(path.toFile().list());

		return fileNames.stream().collect(Collectors.toMap((e)->{
			return Paths.get("/image/default/profile/",e).toString();
		}, (e)->{
			return Boolean.valueOf(false);
		}));

	}
}
