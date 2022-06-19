package com.my.azusato.api.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ModifyCelebrationAPIReqeust {
	
	@NotBlank(message = "{notBlank}")
	@Size(max = 50, message = "{size-string.max}")
	private String title;

	@NotNull(message = "{notNull}")
	private MultipartFile content;
	
	@NotBlank(message = "{notBlank}")
	@Size(max = 10, message = "{size-string.max}")
	private String name;
}
