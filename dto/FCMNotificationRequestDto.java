package com.hako.web.cl.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class FCMNotificationRequestDto {
    private String deviceToken;
    private String title;
    private String body;
    private String purpose;
	private int purpose_num;
    private String purpose_title;
	    

 
    
}