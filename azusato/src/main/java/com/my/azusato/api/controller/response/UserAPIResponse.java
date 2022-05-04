package com.my.azusato.api.controller.response;

import lombok.Data;

@Data
public class UserAPIResponse {

	long no;

	String name;

	String id;

	String imageBase64;

	String imageType;
}
