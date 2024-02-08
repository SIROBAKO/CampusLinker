package com.hako.web.cl.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CL_Alarm {

	private int num;
	private String user;
	private String content;
	private String purpose;
	private int purpose_num;
    private Date create_date;
    private String format_date;
    private String purpose_title;
    
 
    
}
