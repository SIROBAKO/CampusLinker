package com.hako.web.cl.dao;

import java.util.List;

import com.hako.web.cl.entity.CL_Comment;

public interface CLCommentDao {


	int insert(CL_Comment comment);

	int update(CL_Comment comment);

	int del(int num);

	CL_Comment get(int num);

	int getRefCount(int num);

	List<CL_Comment> getList(int board_num);

	int delUser(String id);
}
