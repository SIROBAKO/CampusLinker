package com.hako.web.cl.mybatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.cl.dao.CLChatDao;
import com.hako.web.cl.dto.CLChatRoomDTO;
import com.hako.web.cl.dto.CLChatRoomInfo;
import com.hako.web.cl.entity.CL_Chat;
import com.hako.web.cl.entity.CL_Chat_Room;
import com.hako.web.cl.entity.CL_Chat_User;

@Repository
public class MybaticeCLChatDao implements CLChatDao {

	private CLChatDao mapper;

	@Autowired
	public MybaticeCLChatDao(SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CLChatDao.class);
	}

	@Override
	public int create_room(CL_Chat_Room chat_room) {

		try {
			return mapper.create_room(chat_room);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	@Override
	public int update_room(CL_Chat_Room chat_room) {
		// TODO Auto-generated method stub
		try {
			return mapper.update_room(chat_room);
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
			return 0;
		}

	}

	@Override
	public int del_room(int room_num) {
		// TODO Auto-generated method stub
		try {
			return mapper.del_room(room_num);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	@Override
	public CL_Chat_Room get_room(int room_num) {
		// TODO Auto-generated method stub
		return mapper.get_room(room_num);
	}

	@Override
	public int insert_chat(CL_Chat chat) {
		// TODO Auto-generated method stub
		try {
			return mapper.insert_chat(chat);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	@Override
	public int update_chat(CL_Chat chat) {
		// TODO Auto-generated method stub
		try {
			return mapper.update_chat(chat);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	@Override
	public int del_chat(int num) {
		// TODO Auto-generated method stub
		try {
			return mapper.del_chat(num);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	@Override
	public CL_Chat get_chat(int chat_num) {
		// TODO Auto-generated method stub

		return mapper.get_chat(chat_num);

	}

	@Override
	public List<CL_Chat_User> get_userList(int room_num) {
		// TODO Auto-generated method stub
		return mapper.get_userList(room_num);
	}

	@Override
	public int insert_user(CL_Chat_User chat_user) {
		// TODO Auto-generated method stub
		try {
			return mapper.insert_user(chat_user);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public int del_user(int num, String id) {
		// TODO Auto-generated method stub
		try {
			return mapper.del_user(num, id);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public int count_user(int room_num) {
		// TODO Auto-generated method stub

		return mapper.count_user(room_num);

	}

	@Override
	public CL_Chat_User get_user(int num) {
		// TODO Auto-generated method stub
		return mapper.get_user(num);
	}

	@Override
	public List<CLChatRoomDTO> get_roomList(String id) {
		// TODO Auto-generated method stub
		return mapper.get_roomList(id);
	}

	@Override
	public int check_user(int _room_num, String id) {
		// TODO Auto-generated method stub
		try {
			return mapper.check_user(_room_num, id);

		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public List<CL_Chat> get_chatList(int _room_num, int start, int end) {
		// TODO Auto-generated method stub

		return mapper.get_chatList(_room_num, start, end);
	}

	@Override
	public int insert_users(CL_Chat_User chat_user) {
		try {

			return mapper.insert_users(chat_user);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public int insert_chats(CL_Chat chat) {
		// TODO Auto-generated method stub

		try {
			return mapper.insert_chats(chat);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public CL_Chat get_lastChat() {
		// TODO Auto-generated method stub
		return mapper.get_lastChat();
	}

	@Override
	public int addNotRead(int room, String user_id) {
		// TODO Auto-generated method stub
		try {
			return mapper.addNotRead(room, user_id);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public int resetNotRead(CL_Chat_User chat_user) {
		// TODO Auto-generated method stub

		try {
			return mapper.resetNotRead(chat_user);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;

		}
	}

	@Override
	public int del_chatAll(String id) {
		// TODO Auto-generated method stub
		try {
			return mapper.del_chatAll(id);
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}

	@Override
	public int del_userAll(String id) {
		// TODO Auto-generated method stub
		try {
			return mapper.del_userAll(id);
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}

	@Override
	public int del_roomUser(int room_num) {
		// TODO Auto-generated method stub
		try {
			return mapper.del_roomUser(room_num);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public List<CLChatRoomInfo> get_chatInfo(int room_num) {
		// TODO Auto-generated method stub
		try {
			return mapper.get_chatInfo(room_num);
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		
			return null;
		}
	}

	@Override
	public void firstChat(String name) {
		// TODO Auto-generated method stub
		

		try {
			mapper.firstChat(name);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
	}

	@Override
	public String getRoomName(int room_num) {
		// TODO Auto-generated method stub
		return mapper.getRoomName(room_num);
	}

}
