package com.my.azusato.api.service.request;

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
public class UpdateUserProfileServiceAPIRequest {

	String name;

	String profileImageType;

	String profileImageBase64;

	Long userNo;
}
