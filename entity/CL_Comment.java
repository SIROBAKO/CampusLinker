package com.hako.web.cl.entity;

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
public class CL_Comment {

	private int num;
	private int ref;
	private int ref_comment;
	private String comment;
	private String user_id;

	private Date create_date;
	private String hidden_name;

	private String format_date;
}
