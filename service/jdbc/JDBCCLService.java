package com.hako.web.cl.service.jdbc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hako.web.cl.dao.CLAlarmDao;
import com.hako.web.cl.dao.CLBoardDao;
import com.hako.web.cl.dao.CLChatDao;
import com.hako.web.cl.dao.CLCommentDao;
import com.hako.web.cl.dao.CLMatchBoardDao;
import com.hako.web.cl.dao.CLReportDao;
import com.hako.web.cl.dao.CLSocalDao;
import com.hako.web.cl.dao.CLTokenDao;
import com.hako.web.cl.dao.CLUserDao;
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
import com.hako.web.cl.service.CLService;

@Service
@Transactional
public class JDBCCLService implements CLService {

	@Autowired
	private CLUserDao userDao;

	@Autowired
	private CLCommentDao commentDao;

	@Autowired
	private CLBoardDao boardDao;

	@Autowired
	private CLMatchBoardDao matchBoardDao;

	@Autowired
	private CLChatDao chatDao;

	@Autowired
	private CLTokenDao tokenDao;

	@Autowired
	private CLSocalDao socalDao;

	@Autowired
	private CLReportDao reportDao;

	@Autowired
	private CLAlarmDao alarmDao;

	// 대학 목록 조회
	@Override
	public List<CL_Univ> getUnivList(String query) {

		return userDao.getUnivList(query);

	}

	// 대학 조회
	@Override
	public CL_Univ getUniv(String query) {

		return userDao.getUniv(query);

	}

	// 회원가입
	@Override
	public int joinUesr(CL_User user) {
		return userDao.joinUser(user);

	}

	// 아이디, 이름, 이메일 중복 확인
	@Override
	public int checkUserForm(String query, String value) {

		return userDao.checkUserForm(query, value);
	}

	// 사용자 정보 반환
	@Override
	public CL_User getUser(String id) {

		return userDao.getUser(id);

	}

	// 회원탈퇴
	@Override
	public int delUesr(String id) {

		if (boardDao.delUser(id) != -1 && commentDao.delUser(id) != -1 && chatDao.del_chatAll(id) != -1
				&& chatDao.del_userAll(id) != -1) {
			return userDao.delUser(id);
		} else {
			return 0;
		}

	}

	// 회원정보수정
	@Override
	public int updateUser(CL_User user) {

		return userDao.updateUser(user);

	}

//	댓글 작성
	@Override
	public int insertComment(CL_Comment comment) {

		boardDao.addComment(comment.getRef());
		return commentDao.insert(comment);

	}

//	댓글 수정	
	@Override
	public int updateComment(CL_Comment comment) {

		return commentDao.update(comment);

	}

//	댓글 삭제
	@Override
	public int delComment(int num) {

		CL_Comment comment = commentDao.get(num);

		boardDao.subComment(comment.getRef());
		// 최상위 댓글 일시
		if (comment.getRef_comment() == 0) {

			// 대댓글이 없을 시
			if (commentDao.getRefCount(num) == 0) {
				// 삭제
				return commentDao.del(num);

				// 대댓글이 있을 때 업데이트
			} else {
				CL_Comment update = new CL_Comment();
				update.setUser_id("(알수없음)");
				update.setComment("삭제된 댓글 입니다.");
				update.setHidden_name("N");
				update.setNum(num);

				if (commentDao.update(update) == 1) {
					return 3;
				} else {
					return 0;
				}

			}
			// 대댓글 일때
		} else {
			// 대댓글 삭제
			if (commentDao.del(num) == 1) {

				// 삭제후 최상위 댓글이 삭제된 댓글이라면
				CL_Comment refComment = commentDao.get(comment.getRef_comment());

				if (refComment.getUser_id().equals("(알수없음)")) {
					// 이 댓글도 삭제
					return commentDao.del(refComment.getNum());
				}
				return 1;
			}
			return 0;
		}

	}

	// 댓글 반환
	@Override
	public CL_Comment getComment(int comment_num) {

		return commentDao.get(comment_num);
	}

	// 댓글 리스트 반환
	@Override
	public List<CL_Comment> getCommentList(int board_num) {

		return commentDao.getList(board_num);
	}

	// 게시글 작성
	@Override
	public int insertBoard(CL_Board board) {
		return boardDao.insert(board);
	}

	// 게시글 수정
	@Override
	public int updateBoard(CL_Board board) {

		return boardDao.update(board);
	}

	// 게시글 삭제
	@Override
	public int delBoard(int board_num) {

		return boardDao.del(board_num);

	}

	// 게시글 반환
	@Override
	public CL_Board getBoard(int board_num) {

		return boardDao.get(board_num);
	}

	// 게시글 목록 반환
	@Override
	public List<CL_Board> getBoardList(int page, String school, String category, String title) {

		int start = page * 20 + 1;
		int end = start + 19;
		return boardDao.getList(start, end, school, category, title);
	}

	// 매치 게시물 작성
	@Override
	public int insertMatchBoard(CL_Match_Board board) {

		CL_Chat_Room room = new CL_Chat_Room();
		room.setRoom_name(board.getTitle());
		room.setMax_user(board.getNum());

		CL_Chat_User user = new CL_Chat_User();
		user.setUser_id(board.getWrite_user());
		if (chatDao.create_room(room) == 1 && boardDao.insert(board) == 1 && chatDao.insert_users(user) == 1)
			return matchBoardDao.insert(board);
		else {
			return 0;
		}
	}

	// 매치 게시물수정
	@Override
	public int updateMatchBoard(CL_Match_Board board, CL_Chat_Room room) {

		if (boardDao.update(board) == 1 && chatDao.update_room(room) == 1) {
			return matchBoardDao.update(board);
		} else {
			return 0;
		}
	}

	// 매치 게시물 삭제
	@Override
	public int delMatchBoard(int board_num) {

		CL_Match_Board board = matchBoardDao.get(board_num);
		System.out.println(board.toString());
		boardDao.del(board_num);
		chatDao.del_room(board.getRoom_num());
		chatDao.del_roomUser(board.getRoom_num());
		return matchBoardDao.del(board_num);

	}

	// 매치 게시물 반환
	@Override
	public CL_Match_Board getMatchBoard(int board_num) {

		return matchBoardDao.get(board_num);
	}

	// 매치 게시물 목록 반환
	@Override
	public List<CLMatchBoardDTO> getMatchBoardList(int page, String school, String category, String title) {

		int start = page * 20 + 1;
		int end = start + 19;
		return matchBoardDao.getList(start, end, school, category, title);
	}

	// 채팅방생성
	@Override
	public int createChatRoom(CL_Chat_Room chat_room) {

		return chatDao.create_room(chat_room);
	}

	// 채팅방 업데이트
	@Override
	public int updateChatRoom(CL_Chat_Room chat_room) {

		return chatDao.update_room(chat_room);
	}

	// 채팅방 삭제
	@Override
	public int deleteChatRoom(int room_num) {

		return chatDao.del_room(room_num);
	}

	// 채팅방 조회
	@Override
	public CL_Chat_Room getChatRoom(int room_num) {

		return chatDao.get_room(room_num);
	}

	// 채팅 입력
	@Override
	public int insertChat(CL_Chat chat) {

		chatDao.addNotRead(chat.getRoom(), chat.getUser_id());
		return chatDao.insert_chat(chat);
	}

	// 채팅 수정
	@Override
	public int updateChat(CL_Chat chat) {

		return chatDao.update_chat(chat);
	}

	// 채팅 삭제
	@Override
	public int deleteChat(int chat_num) {

		return chatDao.del_chat(chat_num);
	}

	// 채팅 반환
	@Override
	public CL_Chat getChat(int chat_num) {

		return chatDao.get_chat(chat_num);
	}

	// 토큰 입력
	@Override
	public int insertToken(CL_JwtToken token) {

		return tokenDao.insert(token);
	}

	// 토큰 수정
	@Override
	public int updateToken(CL_JwtToken token) {

		return tokenDao.update(token);
	}

	// 토큰 삭제
	@Override
	public int deleteToken(String id) {

		return tokenDao.del(id);
	}

	// 토큰 반환
	@Override
	public CL_JwtToken getToken(String id) {

		return tokenDao.get(id);
	}

	// 인증 정보 확인
	@Override
	public CL_Cert getCert(String token) {

		return userDao.getCert(token);
	}

	// 인증 생성
	@Override
	public int insertCert(CL_Cert cert) {

		return userDao.insertCert(cert);
	}

	// 인증 정보삭제
	@Override
	public int delCert(String token) {

		return userDao.delCert(token);
	}

	// 이메일로 아이디 찾기
	@Override
	public String findId(String email) {

		return userDao.findId(email);
	}

	// 친구 목록 리스트 반환
	@Override
	public List<CL_Socal> getSocalList(String user_id) {

		return socalDao.getList(user_id);
	}

	// 친구 목록 반환
	@Override
	public CL_Socal getSocal(String user_id, String friend_id) {

		return socalDao.getById(user_id, friend_id);
	}

	@Override
	public CL_Socal getSocal(int socal_num) {

		return socalDao.getByNum(socal_num);
	}

	// 친구 요청
	@Override
	public int insertSocal(CL_Socal socal) {

		return socalDao.insert(socal);
	}

	// 친구 상태 수정
	@Override
	public int updateSocal(CL_Socal socal) {

		return socalDao.update(socal);
	}

	// 친구 삭제
	@Override
	public int delSocal(int num) {

		return socalDao.del(num);
	}

	@Override
	public List<CL_Chat_User> getChatUserList(int room_num) {

		return chatDao.get_userList(room_num);
	}

	// 채팅 유저 추기
	@Override
	public int insertChatUser(CL_Chat_User chat_user) {

		CL_Chat chat = new CL_Chat();

		chat.setRoom(chat_user.getRoom_num());
		chat.setUser_id("시스템 관리자");
		CL_User user = userDao.getUser(chat_user.getUser_id());
		chat.setChat(user.getName() + "님이 입장하셨습니다.");

		chatDao.insert_chat(chat);
		return chatDao.insert_user(chat_user);
	}

	// 채팅 유저추가 여러명
	@Override
	public int insertChatUsers(int[] socal_nums, String id) {

		CL_User invite_user = userDao.getUser(id);
		int check = 1;
		for (int i : socal_nums) {

			if (socalDao.check(i, id) != 1) {
				check = 0;
				return 2;
			}
		}

		String concat_user = "";
		int x = 0;
		for (int i : socal_nums) {
			CL_Socal socal = socalDao.getByNum(i);
			CL_User user = userDao.getUser(socal.getFriend_id());
			if (x == 7) {
				concat_user += user.getName();
			} else {
				concat_user += user.getName() + ",";
				x++;
			}
		}
		concat_user += invite_user.getName();

		CL_Chat_Room room = new CL_Chat_Room();
		room.setRoom_name(concat_user);
		chatDao.create_room(room);

		for (int i : socal_nums) {
			CL_Chat_User chat_user = new CL_Chat_User();
			CL_Socal socal = socalDao.getByNum(i);
			chat_user.setUser_id(socal.getFriend_id());

			if (chatDao.insert_users(chat_user) == 0) {
				return 0;
			}
		}
		for (int i : socal_nums) {
			CL_Chat chat = new CL_Chat();
			CL_Socal socal = socalDao.getByNum(i);

			chat.setUser_id("시스템 관리자");
			chat.setChat(socal.getFriend_id() + "님이 입장하셨습니다.");

			if (chatDao.insert_chats(chat) == 0) {
				return 0;
			}
		}

		CL_Chat_User chat_user = new CL_Chat_User();
		chat_user.setUser_id(id);

		CL_Chat chat = new CL_Chat();
		chat.setUser_id("시스템 관리자");
		chat.setChat(id + "님이 입장하셨습니다.");

		if (chatDao.insert_users(chat_user) == 0) {
			return 0;
		}
		if (chatDao.insert_chats(chat) == 0) {
			return 0;
		}

		return 1;
	}

	@Override
	public int delChatUser(int num, String id) {

		CL_Chat chat = new CL_Chat();

		chat.setRoom(num);
		chat.setUser_id("시스템 관리자");
		CL_User user = userDao.getUser(id);
		chat.setChat(user.getName() + "님이 퇴장하셨습니다.");

		chatDao.insert_chat(chat);
		
		return chatDao.del_user(num, id);
	}

	@Override
	public int countChatUser(int room_num) {

		return chatDao.count_user(room_num);
	}

	@Override
	public CL_Chat_User getChatUser(int num) {

		return chatDao.get_user(num);
	}

	@Override
	public int insertReport(CL_Report report) {

		return reportDao.insert(report);
	}

	// 채팅 룸 목록 반환 , 룸번호 제목, 안읽은 수 , 마지막 채팅 시간 반환
	@Override
	public List<CLChatRoomDTO> getChatRoomList(String id) {

		return chatDao.get_roomList(id);
	}

	// 해당 채팅방 유저인지 확인
	@Override
	public int checkChatUser(int _room_num, String id) {

		return chatDao.check_user(_room_num, id);
	}

	// 채팅 목록 반환
	@Override
	public List<CL_Chat> getChatList(int room_num, int page) {

		int start = page * 50 + 1;
		int end = start + 49;

		return chatDao.get_chatList(room_num, start, end);
	}

	// 채팅방 현황 확인
	@Override
	public List<CLChatRoomInfo> getChatRoomInfo(int room_num) {

		return chatDao.get_chatInfo(room_num);
	}

	// 마지막 채팅 반환
	@Override
	public CL_Chat getLastChat() {

		return chatDao.get_lastChat();
	}

	// 안읽은 채팅수 초기화
	@Override
	public int resetNotRead(CL_Chat_User chat_user) {

		return chatDao.resetNotRead(chat_user);
	}

	// 매치보드 리스트 전부 가져오기
	@Override
	public List<CL_Match_Board> getBoardListAll() {

		return matchBoardDao.getListAll();
	}

	@Override
	public int checkReaction(String user_id, int board_num) {
		// TODO Auto-generated method stub
		return boardDao.checkReaction(user_id, board_num);
	}

	@Override
	public int addReaction(String user_id, int board_num) {
		// TODO Auto-generated method stub
		if (boardDao.addReactionUser(user_id, board_num) == 1 && boardDao.addReaction(board_num) == 1) {

			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public List<CL_Board> getBoardList(String purpose, String user_id, int page) {
		// TODO Auto-generated method stub
		int start = page * 20 + 1;
		int end = start + 19;
		return boardDao.getListPurpose(purpose, user_id, start, end);
	}

	@Override
	public int getUserCount(int room_num) {
		// TODO Auto-generated method stub
		return chatDao.count_user(room_num);
	}

	@Override
	public void firstChat(String name) {
		// TODO Auto-generated method stub

		chatDao.firstChat(name);

	}

	@Override
	public List<CL_Alarm> getAlarm(String user_id) {
		// TODO Auto-generated method stub
		return alarmDao.getList(user_id);
	}

	@Override
	public int setAlarm(CL_Alarm alarm) {
		// TODO Auto-generated method stub
		return alarmDao.setAlarm(alarm);
	}

	@Override
	public int setDeviceToken(CL_JwtToken token) {
		// TODO Auto-generated method stub
		return tokenDao.setDeviceToken(token);
	}

	@Override
	public String getDeviceToken(String user_id) {
		// TODO Auto-generated method stub
		return tokenDao.getDeviceToken(user_id);
	}

	@Override
	public String getRoomName(int room_num) {
		// TODO Auto-generated method stub
		return chatDao.getRoomName(room_num);
	}

	@Override
	public int getBoardNum(int room_num) {
		
		
		return matchBoardDao.getBoardNum(room_num);	
	}

}
