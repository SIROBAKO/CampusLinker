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
public class CL_Board {
	protected int num;
	protected String write_user;
	protected String title;
	protected String category;
	protected String contents;
	protected Date create_date;
	protected int comment;
	protected int reaction_count;
	protected String hidden_name;
	protected String school;
	protected String board_type;
	protected String format_date;

	public CL_Board(String write_user, String title, String category, String contents, String hidden_name,
			String school, String board_type) {
		super();
		this.write_user = write_user;
		this.title = title;
		this.category = category;
		this.contents = contents;
		this.hidden_name = hidden_name;
		this.school = school;
		this.board_type = board_type;
	}

}
