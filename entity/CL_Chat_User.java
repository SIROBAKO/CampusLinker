package com.hako.web.cl.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CL_Chat_User {

	private int num;
	private int room_num;
	private String user_id;
	private int not_read;

}
