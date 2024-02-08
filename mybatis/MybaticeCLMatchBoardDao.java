package com.hako.web.cl.mybatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.cl.dao.CLMatchBoardDao;
import com.hako.web.cl.dto.CLMatchBoardDTO;
import com.hako.web.cl.entity.CL_Match_Board;

@Repository
public class MybaticeCLMatchBoardDao implements CLMatchBoardDao  {

	private CLMatchBoardDao mapper;

	@Autowired
	public MybaticeCLMatchBoardDao(SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CLMatchBoardDao.class);
	}
	

	@Override
	public int insert(CL_Match_Board board) {
		// TODO Auto-generated method stub
		return mapper.insert(board);
//		try {
//			return mapper.insert(board);
//		} catch (Exception e) {
//			// TODO: handle exception
//			return 0;
//		}

	}

	@Override
	public int update(CL_Match_Board board) {
		// TODO Auto-generated method stub
		try{
			return mapper.update(board);
			
		}catch (Exception e) {
			System.out.println(e);
			return 0;
		}
	}

	@Override
	public int del(int board_num) {
		// TODO Auto-generated method stub
		
		try {
			
			return mapper.del(board_num);
		} catch (Exception e) {
            System.out.println(e);
			return 0;
			// TODO: handle exception
		}
	}

	@Override
	public CL_Match_Board get(int board_num) {
		// TODO Auto-generated method stub
		return mapper.get(board_num);
	}

	@Override
	public List<CLMatchBoardDTO> getList(int start, int end, String school,String category,String title) {
		// TODO Auto-generated method stub
		return mapper.getList(start, end, school, category, title);
	}


	@Override
	public List<CL_Match_Board> getListAll() {
		// TODO Auto-generated method stub
		return mapper.getListAll();
	}


	@Override
	public int getBoardNum(int room_num) {
		// TODO Auto-generated method stub
		return mapper.getBoardNum(room_num);
		
	}

}
