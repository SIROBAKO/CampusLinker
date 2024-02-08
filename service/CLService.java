package com.hako.web.cl.service;

import java.util.List;

import com.hako.web.cl.dto.CLChatRoomDTO;
import com.hako.web.cl.dto.CLChatRoomInfo;
import com.hako.web.cl.dto.CLMatchBoardDTO;
import com.hako.web.cl.entity.CL_Alarm;
import com.hako.web.cl.entity.CL_Board;
import com.hako.web.cl.entity.CL_Cert;
import com.hako.web.cl.entity.CL_Chat;
import com.hako.web.cl.entity.CL_Chat_Room;
import com.hako.web.cl.entity.CL_Chat_User;
import com.hako.web.cl.entity.CL_Comment;
import com.hako.web.cl.entity.CL_JwtToken;
import com.hako.web.cl.entity.CL_Match_Board;
import com.hako.web.cl.entity.CL_Report;
import com.hako.web.cl.entity.CL_Socal;
import com.hako.web.cl.entity.CL_Univ;
import com.hako.web.cl.entity.CL_User;

public interface CLService {

	// 인증 정보 가져오기
	CL_Cert getCert(String token);

	// 인증 정보 저장
	int insertCert(CL_Cert cert);

	// 인증 정보 삭제
	int delCert(String token);

//	대학 목록 조회
	List<CL_Univ> getUnivList(String query);

//  대학 조회
	CL_Univ getUniv(String query);

//	중복 데이터 조회
	int checkUserForm(String query,String value);
	
//	회원가입
	int joinUesr(CL_User user);

//	사용자 확인 (로그인)
	CL_User getUser(String id);

//	회원탈퇴	
	int delUesr(String id);

//	회원정보수정
	int updateUser(CL_User user);

	
//	email로 id 찾기
	String findId(String email);

//	댓글작성
	int insertComment(CL_Comment comment);

//	댓글 수정
	int updateComment(CL_Comment comment);

//	댓글 삭제
	int delComment(int num);

	CL_Comment getComment(int comment_num);

//	댓글 반환
	List<CL_Comment> getCommentList(int board_num);

	// 게시글 작성
	int insertBoard(CL_Board board);

	// 게시글 수정
	int updateBoard(CL_Board board);

	// 게시글 삭제
	int delBoard(int board_num);

	// 게시글 반환
	CL_Board getBoard(int board_num);

	// 게시글 목록 반환
	List<CL_Board> getBoardList(int page, String school, String category, String title);

	// 매칭 게시물 생성
	int insertMatchBoard(CL_Match_Board board);

	// 매칭 게시물 수정
	int updateMatchBoard(CL_Match_Board board, CL_Chat_Room room);

	// 메치 게시물 삭제
	int delMatchBoard(int board_num);

	// 매치 게시물 반환
	CL_Match_Board getMatchBoard(int board_num);

	// 매치 게시물 목록 반환
	List<CLMatchBoardDTO> getMatchBoardList(int page, String school, String category, String title);

	// 채팅 방 생성
	int createChatRoom(CL_Chat_Room chat_room);

	// 채팅 방 수정
	int updateChatRoom(CL_Chat_Room chat_room);

	// 채팅 방 삭제
	int deleteChatRoom(int room_num);

	// 채팅 방 조회
	CL_Chat_Room getChatRoom(int room_num);

	// 채팅 입력
	int insertChat(CL_Chat chat);

	// 채팅 수정
	int updateChat(CL_Chat chat);

	// 채팅 삭제
	int deleteChat(int chat_num);

	// 채팅 반환
	CL_Chat getChat(int chat_num);

	// 토큰 입력
	int insertToken(CL_JwtToken token);

	// 토큰 수정
	int updateToken(CL_JwtToken token);

	// 토큰 삭제
	int deleteToken(String id);

	// 토큰 반환(아이디로 )
	CL_JwtToken getToken(String id);

	List<CL_Socal> getSocalList(String user_id);

	int insertSocal(CL_Socal socal);

	int updateSocal(CL_Socal socal);

	int delSocal(int num);

	CL_Socal getSocal(String user_id, String friend_id);
	CL_Socal getSocal(int socal_num);
	
	
	List<CL_Chat_User> getChatUserList(int room_num);

	int insertChatUser(CL_Chat_User chat_user);
	
	int delChatUser(int num, String id);

	int countChatUser(int room_num);

	CL_Chat_User getChatUser(int num);
	
	List<CLChatRoomDTO> getChatRoomList(String id);


	int insertReport(CL_Report report);

	int checkChatUser(int room_num, String id);

	List<CL_Chat> getChatList(int room_num, int page);

	int insertChatUsers(int[] socal_nums, String id);

	CL_Chat getLastChat();

	int resetNotRead(CL_Chat_User chat_user);

	List<CL_Match_Board> getBoardListAll();

	int checkReaction(String user_id, int board_num);

	int addReaction(String user_id, int board_num);

	List<CL_Board> getBoardList(String purpose, String user_id,int page);

	int getUserCount(int room_num);

	List<CLChatRoomInfo> getChatRoomInfo(int room_num);

	void firstChat(String name);

	List<CL_Alarm> getAlarm(String user_id);
	
	int setAlarm(CL_Alarm alarm);

	int setDeviceToken(CL_JwtToken token);

	String getDeviceToken(String user_id);

	String getRoomName(int room_num);

	int getBoardNum(int room_num);

}
