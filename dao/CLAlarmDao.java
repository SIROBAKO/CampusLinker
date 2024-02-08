package com.hako.web.cl.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hako.web.cl.entity.CL_Alarm;
import com.hako.web.cl.entity.CL_Board;


public interface CLAlarmDao {

	List<CL_Alarm> getList(String user_id);
	
	int setAlarm(CL_Alarm alarm);	
}
