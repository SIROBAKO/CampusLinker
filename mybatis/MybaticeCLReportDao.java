package com.hako.web.cl.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.cl.dao.CLReportDao;
import com.hako.web.cl.entity.CL_Report;

@Repository
public class MybaticeCLReportDao implements CLReportDao {
	

	private CLReportDao mapper;
	
	@Autowired
	public MybaticeCLReportDao( SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CLReportDao.class);
	}

	@Override
	public int insert(CL_Report report) {
		// TODO Auto-generated method stub
	
		try {
			return mapper.insert(report);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
	




}
