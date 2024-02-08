package com.hako.web.cl.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CL_JwtToken {

	private int num;
	private String user_id;
	private String access_token;
	private String refresh_token;
	private String device_token;
	
	
	
	
}
