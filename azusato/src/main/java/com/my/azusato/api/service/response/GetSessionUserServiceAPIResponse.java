package com.my.azusato.api.service.response;

import com.my.azusato.api.service.UserServiceAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response of {@link UserServiceAPI#getSessionUser(Long, java.util.Locale)}
 * 
 * @author kim-t
 *
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetSessionUserServiceAPIResponse {

	String name;

	String profileImagePath;

	String id;
}
