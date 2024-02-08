package com.hako.web.cl.entity;
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
public class CL_Chat{

	private int num;
	private int room;
	private String user_id;
	private String chat;
	private Date create_date;
   private String format_date;
	



}
