package com.hako.web.cl.controller.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hako.web.cl.dto.CLChatRoomDTO;
import com.hako.web.cl.dto.CLChatRoomInfo;
import com.hako.web.cl.dto.FCMNotificationRequestDto;
import com.hako.web.cl.entity.CL_Alarm;
import com.hako.web.cl.entity.CL_Chat;
import com.hako.web.cl.entity.CL_Chat_Room;
import com.hako.web.cl.entity.CL_Chat_User;
import com.hako.web.cl.entity.CL_Match_Board;
import com.hako.web.cl.entity.CL_Socal;
import com.hako.web.cl.entity.CL_User;

@RestController
@RequestMapping("/campus-linker/")
public class CLRestControllerChat extends CLRestController {

	// 모든 채팅방 목록 반환 */
	@GetMapping(value = "room/{accToken}", produces = "application/json; charset=utf8")
	public String getRoomList(@PathVariable String accToken) {
		JSONObject result = new JSONObject();

		String check = checkAccUser(accToken, "N");
		if (!check.equals("true")) {
			return check;
		}

		String id = jwtProvider.getUsernameFromToken(accToken);

		try {
			List<CLChatRoomDTO> chat_list = appService.getChatRoomList(id);

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
			for (CLChatRoomDTO clchat : chat_list) {
				Date create_date = clchat.getCreate_date();
				String formattedDate = dateFormat.format(create_date);
				clchat.setFormat_date(formattedDate);
			}

			result.put("result", 200);
			result.put("message", "채팅목록 조회에 성공했습니다.");
			result.put("list", chat_list);

			chatService.createRoom(id, id + "님의 채팅방");

			return result.toString();
		} catch (Exception e) {
			result.put("result", 409);
			result.put("message", "DB조회중 오류가 발생했습니다.");
			return result.toString();
		}

	}

	// 채팅방 현황 정보 반환 */
	@GetMapping(value = "roominfo/{roomNum}/{accToken}", produces = "application/json; charset=utf8")
	public String getRoomInfo(@PathVariable String accToken, @PathVariable int roomNum) {
		JSONObject result = new JSONObject();

		String check = checkAccUser(accToken, "N");
		if (!check.equals("true")) {
			return check;
		}

		try {
			List<CLChatRoomInfo> chat_room_info = appService.getChatRoomInfo(roomNum);

			result.put("result", 200);
			result.put("message", "채팅룸 정보 조회에 성공했습니다.");
			result.put("roomInfo", chat_room_info);

			return result.toString();
		} catch (Exception e) {
			result.put("result", 409);
			result.put("message", "DB조회중 오류가 발생했습니다.");
			return result.toString();
		}

	}

	// 채팅방 조회*/
	@GetMapping(value = "room/{room_num}/{accToken}", produces = "application/json; charset=utf8")
	public String getRoom(@PathVariable String room_num, @PathVariable String accToken,
			@RequestParam(defaultValue = "0") int page) {
		JSONObject result = new JSONObject();

		String check = checkAccUser(accToken, "N");
		if (!check.equals("true")) {
			return check;
		}

		String id = jwtProvider.getUsernameFromToken(accToken);
		int _room_num = Integer.parseInt(room_num);

		try {
			if (appService.checkChatUser(_room_num, id) == 1) {

				List<CL_Chat> chat_list = appService.getChatList(_room_num, page);

				for (CL_Chat cl_Chat : chat_list) {

					if (!cl_Chat.getUser_id().equals("(알수없음)") && !cl_Chat.getUser_id().contains("시스템 관리자")) {
						CL_User user = appService.getUser(cl_Chat.getUser_id());
						cl_Chat.setUser_id(user.getName());
					}
				}

				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
				for (CL_Chat clchat : chat_list) {
					Date create_date = clchat.getCreate_date();
					String formattedDate = dateFormat.format(create_date);
					clchat.setFormat_date(formattedDate);
				}

				List<CL_Chat_User> uset_list = appService.getChatUserList(_room_num);

				String manager = "(글쓴이)";
				for (CL_Chat_User cl_Chat_User : uset_list) {

					cl_Chat_User.setUser_id(appService.getUser(cl_Chat_User.getUser_id()).getName() + manager);
					manager = "";
				}
				result.put("uset_list", uset_list);

				String room_name = appService.getRoomName(_room_num);
				result.put("result", 200);
				result.put("message", "채팅목록 조회에 성공했습니다.");
				result.put("list", chat_list);
				result.put("room_name", room_name);

				chatService.createRoom(room_num, room_num + "번 채팅방");
				sendingOperations.convertAndSend("/sub/chat/room/" + id, "갱신하세여");

				return result.toString();
			} else if (appService.getChatRoom(_room_num) == null) {

				result.put("result", 406);
				result.put("message", "삭제된 채팅방입니다.");
				return result.toString();

			} else {

				result.put("result", 401);
				result.put("message", "조회 권한이 없습니다.");
				return result.toString();
			}
		} catch (Exception e) {

			result.put("result", 409);
			result.put("message", "DB조회중 오류가 발생했습니다.");
			return result.toString();
		}
	}

	// 채팅방 참가, 생성, 초대*/
	// FCM 알림추가, 알림 목록 X
	@PostMapping(value = "room", produces = "application/json; charset=utf8")
	public String enterRoom(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "accToken", "purpose" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {
			return check;
		}

		String accToken = (String) form.get("accToken");
		String purpose = (String) form.get("purpose");
		String user_id = null;

		// 토큰 유효성 체크
		check = checkAccUser(accToken, "Y");
		if (!check.equals("true")) {
			return check;
		} else {
			user_id = jwtProvider.getUsernameFromToken(accToken);
		}

		try {

			// 매치 게시글에서 참가
			if (purpose.equals("visite")) {
				String[] checkValu_visite = { "board_num" };
				check = checkValue(checkValu_visite, form);
				if (!check.equals("true")) {
					return check;
				}

				int board_num = (int) form.get("board_num");

				CL_Match_Board board = appService.getMatchBoard(board_num);
				CL_Chat_Room room = appService.getChatRoom(board.getRoom_num());
				CL_User user = appService.getUser(user_id);

				// 성별 조건 확인
				check = genderCondition(board.getGender(), user.getGender());
				if (!check.equals("true")) {
					return check;
				}
				check = studentIDCondition(board.getStudent_id(), user.getStudent_id());
				if (!check.equals("true")) {
					return check;
				}

				// 학번 조건확인

				if (appService.checkChatUser(board.getRoom_num(), user_id) >= 1) {
					result.put("result", 401);
					result.put("message", "이미 채팅방에 참가되어있습니다.");

					return result.toString();
				}
				if (appService.countChatUser(board.getRoom_num()) < room.getMax_user()) {

					CL_Chat_User chat_user = new CL_Chat_User();
					chat_user.setRoom_num(board.getRoom_num());
					chat_user.setUser_id(user_id);
					chat_user.setRoom_num(board.getRoom_num());

					if (appService.insertChatUser(chat_user) == 1) {

						// n번 채팅방 소켓 구독자에게 메시지 전송
						CL_Chat message = appService.getLastChat();
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
						String formattedDate = dateFormat.format(message.getCreate_date());
						message.setFormat_date(formattedDate);
						message.setUser_id("시스템 관리자");
						
						sendingOperations.convertAndSend("/sub/chat/room/" + board.getRoom_num(), message);

						// 채팅 목록에 있을경우 각 소켓으로 전송
						List<CL_Chat_User> users = appService.getChatUserList(board.getRoom_num());
						for (CL_Chat_User cl_Chat_User : users) {

							sendingOperations.convertAndSend("/sub/chat/room/" + cl_Chat_User.getUser_id(), message);

						}

						// 채팅방 등록 유저들에게 알람 배포
						CL_Alarm alarm = new CL_Alarm();
						alarm.setContent(room.getRoom_name() + "방에\n" + user.getName() + "님이 입장하였습니다.");
						alarm.setPurpose("room");
						alarm.setPurpose_num(board.getRoom_num());

						List<CL_Chat_User> user_list = appService.getChatUserList(board.getRoom_num());

						for (CL_Chat_User cl_Chat_User : user_list) {
							if (!cl_Chat_User.getUser_id().trim().equals(user.getId().trim())) {

								alarm.setUser(cl_Chat_User.getUser_id());
								appService.setAlarm(alarm);

								String deviceToken = appService.getDeviceToken(cl_Chat_User.getUser_id());

								if (deviceToken != null) {

									FCMNotificationRequestDto notificationRequest = new FCMNotificationRequestDto();
									notificationRequest.setDeviceToken(deviceToken);
									notificationRequest.setTitle("시스템 관리자");
									notificationRequest
											.setBody(room.getRoom_name() + "방에\n" + user.getName() + "님이 입장하였습니다.");
									notificationRequest.setPurpose("room");
									notificationRequest.setPurpose_num(board.getRoom_num());

									if (appService.getRoomName(notificationRequest.getPurpose_num()) != null) {
										notificationRequest.setPurpose_title(
												appService.getRoomName(notificationRequest.getPurpose_num()));
									} else {
										notificationRequest.setPurpose_title("삭제된 채팅방 입니다.");
									}

									fcmService.sendNotification(notificationRequest);
								}

							}
						}

						result.put("result", 200);
						result.put("message", "채팅방에 참가하였습니다.");
						return result.toString();
					}

				} else {
					result.put("result", 401);
					result.put("message", "채팅방 최대 인원 초과입니다.");
					return result.toString();
				}
				throw new Exception("채팅방 참가중 오류발생");
				// 친구 채팅 생성
			} else if (purpose.equals("create")) {
				String[] checkValu_create = { "socal_nums" };
				check = checkValue(checkValu_create, form);
				if (!check.equals("true")) {
					return check;
				}

				String _socal_nums = (String) form.get("socal_nums");

				String[] _socal_num = _socal_nums.split(",");

				int[] socal_num = new int[_socal_num.length];
				for (int i = 0; i < _socal_num.length; i++) {
					socal_num[i] = Integer.parseInt(_socal_num[i]);
				}

				if (socal_num.length > 20) {
					result.put("result", 401);
					result.put("message", "채팅 최대인원은 20명 입니다.");
					return result.toString();
				}
				int check_num = 0;
				if ((check_num = appService.insertChatUsers(socal_num, user_id)) == 1) {
					result.put("result", 200);
					result.put("message", "채팅방에 참가하였습니다.");
					return result.toString();
				} else if (check_num == 2) {
					result.put("result", 403);
					result.put("message", "초대인원중 친구가 아닌사람이 있습니다.");
					return result.toString();
				}
				throw new Exception("채팅방 생성중 오류발생");
				// 친구 초대
			} else if (purpose.equals("invite")) {
				String[] checkValu_create = { "socal_num", "room_num" };
				check = checkValue(checkValu_create, form);
				if (!check.equals("true")) {
					return check;
				}

				int socal_num = (int) form.get("socal_num");
				int room_num = (int) form.get("room_num");

				CL_Chat_Room room = appService.getChatRoom(room_num);
				CL_Socal socal = appService.getSocal(socal_num);

				if (!socal.getUser_id().equals(user_id)) {
					result.put("result", 403);
					result.put("message", "친구 초대 권한이 없습니다.");
					return result.toString();
				}
				if (appService.checkChatUser(room_num, user_id) != 1) {
					result.put("result", 403);
					result.put("message", "채팅방 초대 권한이 없습니다.");
					return result.toString();
				}

				if (appService.countChatUser(room_num) <= room.getMax_user()) {

					CL_Chat_User chat_user = new CL_Chat_User();
					chat_user.setRoom_num(room_num);
					chat_user.setUser_id(socal.getFriend_id());

					if (appService.insertChatUser(chat_user) == 1) {
						result.put("result", 200);
						result.put("message", "채팅방에 초대 하였습니다.");
						return result.toString();
					}

					throw new Exception("채팅방 초대중 오류발생");
				} else {
					result.put("result", 401);
					result.put("message", "채팅방 최대 인원 초과입니다.");
					return result.toString();
				}
			} else {
				result.put("result", 404);
				result.put("message", "잘못된 접근 입니다.");
				return result.toString();
			}

		} catch (

		Exception e) {

			result.put("result", 409);
			result.put("message", "채팅방 참가중 오류가 생겼습니다.");
			return result.toString();
		}

	}

	// 채팅 룸 삭제(나가기)*/
	@DeleteMapping(value = "room/{room_num}/{accToken}", produces = "application/json; charset=utf8")
	public String delRoom(@PathVariable String room_num, @PathVariable String accToken,
			@RequestParam(defaultValue = "0") int page) {
		JSONObject result = new JSONObject();

		String check = checkAccUser(accToken, "N");
		if (!check.equals("true")) {
			return check;
		}

		String id = jwtProvider.getUsernameFromToken(accToken);
		int _room_num = Integer.parseInt(room_num);

		try {
			if (appService.checkChatUser(_room_num, id) == 1) {
				if (appService.delChatUser(_room_num, id) == 1) {

					List<CLChatRoomInfo> roomInfo = appService.getChatRoomInfo(_room_num);

					for (CLChatRoomInfo clChatRoomInfo : roomInfo) {

						if (clChatRoomInfo.getUserCount() == 0) {

							appService.delMatchBoard(appService.getBoardNum(_room_num));
						}
					}

					result.put("result", 200);
					result.put("message", "채팅방 삭제에 성공했습니다.");

					CL_User user = appService.getUser(id);
					// n번 채팅방 소켓 구독자에게 메시지 전송
					CL_Chat message = appService.getLastChat();
					message.setUser_id("시스템 관리자");
					message.setChat("채팅방 삭제 완료");

					sendingOperations.convertAndSend("/sub/chat/room/" + user.getId(), message);

					return result.toString();
				}
				throw new Exception("DB 작업중 문제 발생");
			} else {

				result.put("result", 401);
				result.put("message", "삭제 권한이 없습니다.");
				return result.toString();
			}
		} catch (Exception e) {

			result.put("result", 409);
			result.put("message", "DB삭제중 오류가 발생했습니다.");
			return result.toString();
		}
	}

	// 채팅 보내기 */
	@PostMapping(value = "chat", produces = "application/json; charset=utf8")
	public String sendChat(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "accToken", "chat", "room_num" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {
			return check;
		}

		String accToken = (String) form.get("accToken");
		String user_id = null;
		String _chat = cleanXSS((String) form.get("chat"));
		int room_num = (int) form.get("room_num");

		// 토큰 유효성 체크
		check = checkAccUser(accToken, "Y");
		if (!check.equals("true")) {
			return check;
		} else {
			user_id = jwtProvider.getUsernameFromToken(accToken);
		}

		try {

			CL_Chat chat = new CL_Chat(0, room_num, user_id, _chat, null, null);
			if (appService.checkChatUser(room_num, user_id) == 1 && appService.insertChat(chat) == 1) {
				result.put("result", 200);
				result.put("message", "채팅을 보냈습니다.");

				// n번 채팅방 소켓 구독자에게 메시지 전송
				CL_Chat message = appService.getLastChat();

				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
				String formattedDate = dateFormat.format(message.getCreate_date());
				message.setFormat_date(formattedDate);

				CL_User user = appService.getUser(message.getUser_id());

				message.setUser_id(user.getName());

				sendingOperations.convertAndSend("/sub/chat/room/" + message.getRoom(), message);

				// 채팅 목록에 있을경우 각 소켓으로 전송
				List<CL_Chat_User> users = appService.getChatUserList(room_num);
				for (CL_Chat_User cl_Chat_User : users) {

					sendingOperations.convertAndSend("/sub/chat/room/" + cl_Chat_User.getUser_id(), message);

					if (!cl_Chat_User.getUser_id().trim().equals(user.getId().trim())) {
						String deviceToken = appService.getDeviceToken(cl_Chat_User.getUser_id());
						if ( deviceToken != null) {

							FCMNotificationRequestDto notificationRequest = new FCMNotificationRequestDto();
							notificationRequest.setDeviceToken(deviceToken);
							notificationRequest.setTitle(user.getName());
							notificationRequest.setBody(_chat);
							notificationRequest.setPurpose("room");
							notificationRequest.setPurpose_num(room_num);
							if (appService.getRoomName(notificationRequest.getPurpose_num()) != null) {
								notificationRequest
										.setPurpose_title(appService.getRoomName(notificationRequest.getPurpose_num()));
							} else {
								notificationRequest.setPurpose_title("삭제된 채팅방 입니다.");
							}

							fcmService.sendNotification(notificationRequest);
						}
					}
				}

				return result.toString();

			} else {
				result.put("result", 403);
				result.put("message", "채팅 권한이 없습니다.");
				return result.toString();
			}

		} catch (Exception e) {

			result.put("result", 409);
			result.put("message", "채팅중 오류가 생겼습니다.");
			return result.toString();
		}

	}

	// 안읽은 채팅수 초기화 */
	@PutMapping(value = "room", produces = "application/json; charset=utf8")
	public String resetNotRead(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "accToken", "room_num" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {
			return check;
		}

		String accToken = (String) form.get("accToken");
		String user_id = null;
		int room_num = (int) form.get("room_num");

		// 토큰 유효성 체크
		check = checkAccUser(accToken, "Y");
		if (!check.equals("true")) {
			return check;
		} else {
			user_id = jwtProvider.getUsernameFromToken(accToken);
		}

		try {
			CL_Chat_User chat_user = new CL_Chat_User(0, room_num, user_id, 0);
			if (appService.resetNotRead(chat_user) == 1) {
				result.put("result", 200);
				result.put("message", "수정 성공");
				return result.toString();
			}

			throw new Exception("수정중 실패");
		} catch (Exception e) {
			result.put("result", 409);
			result.put("message", "수정중 오류가 생겼습니다.");
			return result.toString();
		}

	}

}
