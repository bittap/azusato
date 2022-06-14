package com.my.azusato.api.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class AddCelebrationAPIReqeust {

	@NotBlank(message = "{notBlank}")
	@Size(max = 50, message = "{size-string.max}")
	String title;

	@NotBlank(message = "{notBlank}")
	// TODO 正規表現基礎勉強
	// https://stackoverflow.com/questions/1240275/how-to-negate-specific-word-in-regex
	@Pattern(regexp = "^(?!.*<script>).*$", message = "{regex}")
	String content;
	
	@NotBlank(message = "{notBlank}")
	@Size(max = 10, message = "{size-string.max}")
	String name;
}
