package com.hako.web.cl.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CL_Cert {
	private String token;
	private String user_id;
	private String purpose;
	private Date delete_date;
	
	public CL_Cert(String token, String user_id, String purpose) {

		this.token = token;
		this.user_id = user_id;
		this.purpose = purpose;
	}

	

}
