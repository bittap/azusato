package com.my.azusato.api.service.request;

import java.io.InputStream;

import com.my.azusato.api.service.ProfileServiceAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * parameter of
 * {@link ProfileServiceAPI#updateUserProfile(UpdateUserProfileServiceAPIRequest)}
 * 
 * @author kim-t
 *
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ModifyUserProfileServiceAPIRequest {

	InputStream profileImage;

	Long userNo;
	
	String profileImageType;
}
