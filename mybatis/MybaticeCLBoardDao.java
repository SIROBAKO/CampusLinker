package com.hako.web.cl.mybatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.cl.dao.CLBoardDao;
import com.hako.web.cl.entity.CL_Board;

@Repository
public class MybaticeCLBoardDao implements CLBoardDao {

	private CLBoardDao mapper;

	@Autowired
	public MybaticeCLBoardDao(SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CLBoardDao.class);
	}

	@Override
	public int insert(CL_Board board) {

		// TODO Auto-generated method stub
		try {
			return mapper.insert(board);
		} catch (Exception e) {

			// TODO: handle exception
			return 0;
		}

	}

	@Override
	public int update(CL_Board board) {
		// TODO Auto-generated method stub
		try {
			return mapper.update(board);
		} catch (Exception e) {

			// TODO: handle exception
			return 0;
		}

	}

	@Override
	public int del(int board_num) {
		try {
			return mapper.del(board_num);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	@Override
	public CL_Board get(int board_num) {
		// TODO Auto-generated method stub

		return mapper.get(board_num);
	}

	@Override
	public List<CL_Board> getList(int start, int end, String school, String category, String title) {
		// TODO Auto-generated method stub
		return mapper.getList(start, end, school, category, title);
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

	@Override
	public int addComment(int board_num) {
		// TODO Auto-generated method stub
		try {
			return mapper.addComment(board_num);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public int subComment(int board_num) {
		// TODO Auto-generated method stub
		try {
			return mapper.subComment(board_num);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public int addReactionUser(String user_id, int board_num) {
		// TODO Auto-generated method stub

		try {
			return mapper.addReactionUser(user_id, board_num) ;
			
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
	
	@Override
	public int addReaction(int board_num) {
		try {
			return mapper.addReaction( board_num) ;
			
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public int checkReaction(String user_id, int board_num) {
		// TODO Auto-generated method stub
		try {
			return mapper.checkReaction(user_id, board_num);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public List<CL_Board> getListPurpose(String purpose, String user_id, int start, int end) {
		// TODO Auto-generated method stub
		return mapper.getListPurpose(purpose, user_id, start, end);
	}

	



}
