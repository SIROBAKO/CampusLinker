package com.hako.web.cl.mybatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.cl.dao.CLCommentDao;
import com.hako.web.cl.entity.CL_Comment;

@Repository
public class MybaticeCLCommentDao implements CLCommentDao {
	

	private CLCommentDao mapper;

	@Autowired
	public MybaticeCLCommentDao( SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CLCommentDao.class);
	}
	
	
	@Override
	public int insert(CL_Comment comment) {
		// TODO Auto-generated method stub
		try {
			return mapper.insert(comment);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	
	}


	@Override
	public int update(CL_Comment comment) {
		// TODO Auto-generated method stub
		try {
			return mapper.update(comment);
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
	public CL_Comment get(int num) {
		// TODO Auto-generated method stub
		return mapper.get(num);
	}


	@Override
	public int getRefCount(int num) {
		// TODO Auto-generated method stub
		try {
			return mapper.getRefCount(num);
		
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}


	@Override
	public List<CL_Comment> getList(int board_num) {
		// TODO Auto-generated method stub
		return mapper.getList(board_num);
	}


	@Override
	public int delUser(String id) {
		// TODO Auto-generated method stub
		
		try {
			return mapper.delUser(id);
		
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}


	
}
