package com.hako.web.cl.mybatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.cl.dao.CLSocalDao;
import com.hako.web.cl.entity.CL_Socal;

@Repository
public class MybaticeCLSocalDao implements CLSocalDao {
	

	private CLSocalDao mapper;
	
	@Autowired
	public MybaticeCLSocalDao( SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CLSocalDao.class);
	}

	@Override
	public List<CL_Socal> getList(String user_id) {
		// TODO Auto-generated method stub
		return mapper.getList(user_id);
	}

	@Override
	public int insert(CL_Socal socal) {
		// TODO Auto-generated method stub
		try {
			return mapper.insert(socal);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}

	@Override
	public int update(CL_Socal socal) {
		// TODO Auto-generated method stub
		try {
			return mapper.update(socal);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}

	@Override
	public int del(int num) {
		// TODO Auto-generated method stub
		try {
			return mapper.del(num);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
		
	}

	@Override
	public CL_Socal getById(String user_id, String friend_id) {
		// TODO Auto-generated method stub
		return mapper.getById(user_id, friend_id);
	}



	@Override
	public CL_Socal getByNum(int socal_num) {
		// TODO Auto-generated method stub
		return mapper.getByNum(socal_num);
	}

	@Override
	public int check(int num, String id) {
		// TODO Auto-generated method stub
		return mapper.check(num, id);
	}
	


}
