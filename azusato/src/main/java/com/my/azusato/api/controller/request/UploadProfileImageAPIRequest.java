package com.my.azusato.api.controller.request;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UploadProfileImageAPIRequest {

	@NotNull(message = "{notNull}")
	private MultipartFile profileImage;
}
