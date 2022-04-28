package com.my.azusato.dto;

import lombok.Data;

/**
 * information of user logged in.
 * 
 * @author kim-t
 *
 */
@Data
public class LoginUserDto {

	private Long no;

	private String userType;
}
