package com.hako.web.cl.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hako.web.cl.entity.CL_Board;


public interface CLBoardDao {
	
	

	int insert(CL_Board board);

	int update(CL_Board board);

    int del(int board_num);
  

	CL_Board get(int board_num);

	List<CL_Board> getList(int start,int end, String school,String category,String title);

	int delUser(String id);

	int addComment(int board_num);

	int subComment(int board_num);

	int addReaction( int board_num);
	
	int addReactionUser(String user_id, int board_num);

	int checkReaction(String user_id, int board_num);

	List<CL_Board> getListPurpose(String purpose, String user_id,int start, int end);

	


	
}
