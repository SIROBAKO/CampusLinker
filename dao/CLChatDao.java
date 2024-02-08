package com.hako.web.cl.dao;

import java.util.List;

import com.hako.web.cl.dto.CLChatRoomDTO;
import com.hako.web.cl.dto.CLChatRoomInfo;
import com.hako.web.cl.entity.CL_Chat;
import com.hako.web.cl.entity.CL_Chat_Room;
import com.hako.web.cl.entity.CL_Chat_User;

public interface CLChatDao {

	int create_room(CL_Chat_Room chat_room);

	int update_room(CL_Chat_Room chat_room);

	int del_room(int room_num);

	CL_Chat_Room get_room(int room_num);

	int insert_chat(CL_Chat chat);

	int update_chat(CL_Chat chat);

	int del_chat(int chat_num);

	CL_Chat get_chat(int chat_num);

	List<CL_Chat_User> get_userList(int room_num);

	int insert_user(CL_Chat_User chat_user);

	int del_user(int num,String id);

	int count_user(int room_num);

	CL_Chat_User get_user(int num);

	List<CLChatRoomDTO> get_roomList(String id);

	int check_user(int room_num, String id);

	List<CL_Chat> get_chatList(int room_num ,int start,int end);

	int insert_users(CL_Chat_User chat_user);

	int insert_chats(CL_Chat chat);

	CL_Chat get_lastChat();

    int addNotRead(int room, String user_id);

	int resetNotRead(CL_Chat_User chat_user);

	int del_chatAll(String id);

	int del_userAll(String id);

	int del_roomUser(int room_num);

	List<CLChatRoomInfo> get_chatInfo(int room_num);

	void firstChat(String write_user);

	String getRoomName(int room_num);




	
}
