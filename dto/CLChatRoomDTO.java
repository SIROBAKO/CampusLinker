package com.hako.web.cl.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CLChatRoomDTO {
	
	private int room_num;
	private String room_name;
	private String last_chat;
	private int not_read;
	private Date create_date;
	private String format_date;
	
}
