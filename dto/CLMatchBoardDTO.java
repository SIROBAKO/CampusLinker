package com.hako.web.cl.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CLMatchBoardDTO {
	
	private int num;
	  private String title;
	  private String category;
	  private String contents;
	  private String gender;
	  private int room_num;
	  private String users;
	  private Date create_date;
	  private Date delete_date;
	  private String format_create_date;
	  private String format_delete_date;

}
