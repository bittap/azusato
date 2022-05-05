package com.my.azusato.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * information of user logged in.
 * 
 * @author kim-t
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDto {

	private Long no;

	private String userType;
}
