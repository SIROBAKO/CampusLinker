package com.hako.web.cl.dao;

import java.util.List;

import com.hako.web.cl.dto.CLMatchBoardDTO;
import com.hako.web.cl.entity.CL_Match_Board;

public interface CLMatchBoardDao {

	int insert(CL_Match_Board board);

	int update(CL_Match_Board board);

	int del(int board_num);

	CL_Match_Board get(int board_num);

	List<CLMatchBoardDTO> getList(int start, int end, String school,String category,String title);

	List<CL_Match_Board> getListAll();

	int getBoardNum(int room_num);

}
