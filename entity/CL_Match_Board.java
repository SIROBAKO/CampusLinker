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
public class CL_Match_Board extends CL_Board {

	
	private String location;
	private String gender;
	private String student_id;
	private int room_num;
	private Date delete_date;
	private String format_create_date;
	private String format_delete_date;

	public CL_Match_Board(String write_user, String title, String category, String contents, String hidden_name,
			String school, String board_type, String location, String gender, String student_id, Date delete_date) {
		super();
		this.write_user = write_user;
		this.title = title;
		this.category = category;
		this.contents = contents;
		this.hidden_name = hidden_name;
		this.school = school;
		this.board_type = board_type;
		this.location = location;
		this.gender = gender;
		this.student_id = student_id;
		this.delete_date = delete_date;
	}

}
