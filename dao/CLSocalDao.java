package com.hako.web.cl.dao;

import java.util.List;

import com.hako.web.cl.entity.CL_Socal;

public interface CLSocalDao {

	List<CL_Socal> getList(String user_id);

	int insert(CL_Socal socal);

	int update(CL_Socal socal);

	int del(int num);

	CL_Socal getById(String user_id, String friend_id);
	CL_Socal getByNum(int socal_num);

	int check(int num, String id);

	

}
