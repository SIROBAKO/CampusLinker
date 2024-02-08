package com.hako.web.cl.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.cl.dao.CLTokenDao;
import com.hako.web.cl.entity.CL_JwtToken;

@Repository
public class MybaticeCLTokenDao implements CLTokenDao {
	

	private CLTokenDao mapper;
	
	@Autowired
	public MybaticeCLTokenDao( SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CLTokenDao.class);
	}
	

	@Override
	public int insert(CL_JwtToken token) {
		// TODO Auto-generated method stub
		try {
			return mapper.insert(token);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}


	@Override
	public int update(CL_JwtToken token) {
		// TODO Auto-generated method stub
		try {
			return mapper.update(token);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}


	@Override
	public int del(String id) {
		// TODO Auto-generated method stub
		try {
			return mapper.del(id);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	
	}


	@Override
	public CL_JwtToken get(String id) {
		// TODO Auto-generated method stub
		return mapper.get(id);
	}


	@Override
	public int setDeviceToken(CL_JwtToken token) {
		// TODO Auto-generated method stub
		try {
			return mapper.setDeviceToken(token);
			
		} catch (Exception e) {
			
			return 0;// TODO: handle exception
		}
		
	}


	@Override
	public String getDeviceToken(String user_id) {
		// TODO Auto-generated method stub
		return  mapper.getDeviceToken(user_id);
	}

}
