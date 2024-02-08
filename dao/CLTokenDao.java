package com.hako.web.cl.dao;

import com.hako.web.cl.entity.CL_JwtToken;

public interface CLTokenDao {

	int insert(CL_JwtToken token);

	int update(CL_JwtToken token);

	int del(String id);

	CL_JwtToken get(String id);

	int setDeviceToken(CL_JwtToken token);

	String getDeviceToken(String user_id);
}
