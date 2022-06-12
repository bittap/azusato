package com.my.azusato.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.api.controller.request.AddNonMemberUserAPIRequest;

public class Test {

	public void test()  {

	}
	
	public static void main(String[] args) throws JsonProcessingException {
		AddNonMemberUserAPIRequest req = new AddNonMemberUserAPIRequest("asd");
		req.setName("asd");
		
		ObjectMapper om = new ObjectMapper();
		
		String json = om.writeValueAsString(req);
		AddNonMemberUserAPIRequest pojo = om.readValue(json, AddNonMemberUserAPIRequest.class);
		System.out.println(pojo);
	}
}
