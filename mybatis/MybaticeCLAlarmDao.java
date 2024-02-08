package com.hako.web.cl.mybatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.cl.dao.CLAlarmDao;
import com.hako.web.cl.dao.CLUserDao;
import com.hako.web.cl.entity.CL_Alarm;

@Repository
public class MybaticeCLAlarmDao implements CLAlarmDao {
	

	private CLAlarmDao mapper;
	
	@Autowired
	public MybaticeCLAlarmDao( SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CLAlarmDao.class);
	}

	@Override
	public List<CL_Alarm> getList(String user_id) {
		// TODO Auto-generated method stub
		try {
		
			return mapper.getList(user_id);
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return null;
		}
	}

	@Override
	public int setAlarm(CL_Alarm alarm) {
		// TODO Auto-generated method stub
		try {
			return mapper.setAlarm(alarm);
	
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return 0;
		}
	}
	

}
