package com.my.azusato.api.service.response;

import com.my.azusato.api.service.UserServiceAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * parameter of
 * {@link UserServiceAPI#addNonMember(AddNonMemberUserServiceAPIRequest)}
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

	String profileImageType;

	String profileImageBase64;

	String id;
}
