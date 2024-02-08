package com.hako.web.cl.mybatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.cl.dao.CLUserDao;
import com.hako.web.cl.entity.CL_Cert;
import com.hako.web.cl.entity.CL_Univ;
import com.hako.web.cl.entity.CL_User;

@Repository
public class MybaticeCLUserDao implements CLUserDao {
	

	private CLUserDao mapper;
	
	@Autowired
	public MybaticeCLUserDao( SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CLUserDao.class);
	}
	

	@Override
	public List<CL_Univ> getUnivList(String query) {
		return mapper.getUnivList(query);
	}

	@Override
	public CL_Univ getUniv(String query) {
		// TODO Auto-generated method stub
		return mapper.getUniv(query);
	}


	@Override
	public int joinUser(CL_User user) {	 
		try {
			return mapper.joinUser(user);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}


	@Override
	public CL_User getUser(String id) {
		// TODO Auto-generated method stub
		return mapper.getUser(id);
	}


	@Override
	public int delUser(String id) {
		// TODO Auto-generated method stub
		try {
			return mapper.delUser(id);	
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}


	@Override
	public int updateUser(CL_User user) {
		
		// TODO Auto-generated method stub
		try {
			return mapper.updateUser(user);	
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		
	}


	@Override
	public int checkUserForm(String qurey, String value) {
		// TODO Auto-generated method stub
		try {
			return mapper.checkUserForm(qurey,value);	
		} catch (Exception e) {
			// TODO: handle exception
			return 1;
		}
		
	}


	


	@Override
	public int insertCert(CL_Cert cert) {
		// TODO Auto-generated method stub
		return mapper.insertCert(cert);	
//		try {
//			return mapper.insertCert(cert);	
//		} catch (Exception e) {
//			// TODO: handle exception
//			return 0;
//		}
	}


	@Override
	public CL_Cert getCert(String token) {
		// TODO Auto-generated method stub
		return mapper.getCert(token);
	}


	@Override
	public int delCert(String token) {
		// TODO Auto-generated method stub
		try {
			return mapper.delCert(token);	
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}


	@Override
	public String findId(String email) {
		// TODO Auto-generated method stub
		return mapper.findId(email);
	}




}
