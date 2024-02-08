package com.hako.web.cl.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.hako.web.cl.controller.socat.ChatRoom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CLChatService {

	private  Map<String, ChatRoom> chatRooms;
	
	@PostConstruct
	// 의존관게 주입완료되면 실행되는 코드
	private void init() {
		chatRooms = new LinkedHashMap<>();
	}

	// 채팅방 불러오기
	public List<ChatRoom> findAllRoom() {
		// 채팅방 최근 생성 순으로 반환
		List<ChatRoom> result = new ArrayList<>(chatRooms.values());
		Collections.reverse(result);
		return result;
	}

	// 채팅방 하나 불러오기
	public ChatRoom findById(String roomId) {
		// 채팅방이 없으면 새로 만들기
		if (chatRooms.get(roomId) == null) {
			return createRoom(roomId, "채팅방 이름");
		} else {
			return chatRooms.get(roomId);
		}
	}

	// 채팅방 생성
	public ChatRoom createRoom(String room_num ,String name) {
		ChatRoom chatRoom = new ChatRoom(room_num, name);
		chatRooms.put(chatRoom.getRoom_num(), chatRoom);
		return chatRoom;
	}
}