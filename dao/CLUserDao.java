package com.hako.web.cl.dao;

import java.util.List;

import com.hako.web.cl.entity.CL_Cert;
import com.hako.web.cl.entity.CL_Univ;
import com.hako.web.cl.entity.CL_User;

public interface CLUserDao {

	List<CL_Univ> getUnivList(String query);

	int joinUser(CL_User user);

	CL_User getUser(String id);

	int delUser(String id);

	int updateUser(CL_User user);

	CL_Univ getUniv(String query);

	int insertCert(CL_Cert cert);

	CL_Cert getCert(String token);

	int delCert(String token);

	String findId(String email);

	int checkUserForm(String query, String value);
}
