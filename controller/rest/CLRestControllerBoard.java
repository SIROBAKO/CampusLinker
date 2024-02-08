package com.hako.web.cl.controller.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

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

import com.hako.web.cl.dto.CLMatchBoardDTO;
import com.hako.web.cl.entity.CL_Board;
import com.hako.web.cl.entity.CL_Chat;
import com.hako.web.cl.entity.CL_Chat_Room;
import com.hako.web.cl.entity.CL_Chat_User;
import com.hako.web.cl.entity.CL_Match_Board;
import com.hako.web.cl.entity.CL_User;

@RestController
@RequestMapping("/campus-linker/")
public class CLRestControllerBoard extends CLRestController {

	// 게시글 목록 조회 */
	@GetMapping(value = "board/{board_case}", produces = "application/json; charset=utf8")
	public String GetBoardList(@RequestParam(defaultValue = "") String accToken,
			@RequestParam(defaultValue = "0") int page, @PathVariable("board_case") String board_case,
			@RequestParam(defaultValue = "") String category, @RequestParam(defaultValue = "") String title) {
		JSONObject result = new JSONObject();

		// accToken 비어있을경우 반환
		if (accToken.equals("")) {
			result.put("result", 400);
			result.put("message", "accessToken 값이 없습니다.");
			return result.toString();
			// 토큰 유효성 체크 및 학교인증 여부 확인
		} else {
			String check = checkAccUser(accToken, "N");
			if (!check.equals("true")) {
				return check;
			}
		}

		// 유저 학교정보 가져오기
		String school = appService.getUser(jwtProvider.getUsernameFromToken(accToken)).getUniv();

		// 매치 게시판 get
		if (board_case.equals("match")) {

			try {
				List<CLMatchBoardDTO> board = appService.getMatchBoardList(page, school, category, title);

				// MM/dd HH:mm 형식으로 포맷 변경된 create_date 업데이트
				SimpleDateFormat dateFormat_create_date = new SimpleDateFormat("MM/dd HH:mm");

				// MM월 dd일 HH시 mm분 까지 형식으로 delete_date 업데이트
				SimpleDateFormat dateFormat_delete_date = new SimpleDateFormat("MM월 dd일 HH시 mm분 까지");
				for (CLMatchBoardDTO clBoard : board) {
					Date create_date = clBoard.getCreate_date();
					Date delete_date = clBoard.getDelete_date();
					String formattedCreateDate = dateFormat_create_date.format(create_date);
					clBoard.setFormat_create_date(formattedCreateDate);
					String formattedDeleteDate = dateFormat_delete_date.format(delete_date);
					clBoard.setFormat_delete_date(formattedDeleteDate);
				}

				for (CLMatchBoardDTO clMatchBoardDTO : board) {

					CL_Chat_Room chat_room = appService.getChatRoom(clMatchBoardDTO.getRoom_num());
					int countUser = appService.getUserCount(clMatchBoardDTO.getRoom_num());

					clMatchBoardDTO.setRoom_num(0);
					clMatchBoardDTO.setUsers(countUser + "/" + chat_room.getMax_user());
				}

				if (board.size() == 0) {
					result.put("result", 406);
					result.put("message", "조회 결과가 없습니다.");
					return result.toString();
				} else {

					result.put("result", 200);
					result.put("message", "매치게시판 글 목록 조회 완료.");
					result.put("list", board);
					return result.toString();
				}
			} catch (Exception e) {
				// TODO: handle exception
				result.put("result", 409);
				result.put("message", "DB조회중 오류가 발생했습니다.");
				return result.toString();
			}
			// 자유 게시판 get
		} else if (board_case.equals("free")) {

			try {

				List<CL_Board> board = appService.getBoardList(page, school, category, title);

				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
				for (CL_Board clBoard : board) {
					Date create_date = clBoard.getCreate_date();
					String formattedDate = dateFormat.format(create_date);
					clBoard.setFormat_date(formattedDate);
				}

				if (board.size() == 0) {
					result.put("result", 406);
					result.put("message", "조회 결과가 없습니다.");
					return result.toString();
				} else {

					result.put("result", 200);
					result.put("message", "자유게시판 글목록 조회 완료.");
					result.put("list", board);
					return result.toString();
				}
			} catch (Exception e) {
				// TODO: handle exception

				result.put("result", 409);
				result.put("message", "DB조회중 오류가 발생했습니다.");
				return result.toString();
			}
		} else {
			String user_id = jwtProvider.getUsernameFromToken(accToken);

			try {
				List<CL_Board> board = appService.getBoardList(board_case, user_id, page);

				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
				for (CL_Board clBoard : board) {
					Date create_date = clBoard.getCreate_date();
					String formattedDate = dateFormat.format(create_date);
					clBoard.setFormat_date(formattedDate);
				}

				result.put("result", 200);
				result.put("list", board);
				result.put("message", "게시글 조회 성공.");
				return result.toString();

			} catch (Exception e) {
				result.put("result", 409);
				result.put("message", "게시글 조회중 오류가 발생했습니다.");
				return result.toString();
			}

		}

	}

	// 게시글 조회 */
	@GetMapping(value = "board/{board_case}/{board_num}", produces = "application/json; charset=utf8")
	public String GetBoard(@RequestParam(defaultValue = "") String accToken,
			@PathVariable("board_case") String board_case, @PathVariable("board_num") int board_num) {

		JSONObject result = new JSONObject();

		// accToken 비어있을 경우 유효성 검사
		if (accToken.equals("")) {
			result.put("result", 400);
			result.put("message", "accessToken 값이 없습니다.");
			return result.toString();
			// 토큰 및 학교인증 여부 확인
		} else {
			String check = checkAccUser(accToken, "Y");
			if (!check.equals("true")) {
				return check;
			}
		}

		// 매치 게시판 get
		if (board_case.equals("match")) {

			try {
				List<CL_Match_Board> board = new ArrayList<>();
				CL_Match_Board temp_board = appService.getMatchBoard(board_num);

				

				if (temp_board.getHidden_name().equals("Y")) {
					temp_board.setWrite_user(temp_board.getWrite_user() + "," + "익명");
				} else if (!temp_board.getWrite_user().equals("(알수없음)")) {
					CL_User temp_user = appService.getUser(temp_board.getWrite_user());
					temp_board.setWrite_user(temp_board.getWrite_user() + "," + temp_user.getName());
				} else {
					temp_board.setWrite_user("(알수없음)" + "," + "(알수없음)");
				}

				board.add(temp_board);

				// MM/dd HH:mm 형식으로 포맷 변경된 create_date 업데이트
				SimpleDateFormat dateFormat_create_date = new SimpleDateFormat("MM/dd HH:mm");

				// MM월 dd일 HH시 mm분 까지 형식으로 delete_date 업데이트
				SimpleDateFormat dateFormat_delete_date = new SimpleDateFormat("MM월 dd일 HH시 mm분 까지");
				for (CL_Match_Board clBoard : board) {
					Date create_date = clBoard.getCreate_date();
					Date delete_date = clBoard.getDelete_date();
					String formattedCreateDate = dateFormat_create_date.format(create_date);
					clBoard.setFormat_create_date(formattedCreateDate);
					String formattedDeleteDate = dateFormat_delete_date.format(delete_date);
					clBoard.setFormat_delete_date(formattedDeleteDate);
				}

				if (board.get(0) == null) {
					result.put("result", 406);
					result.put("alert", "Y");
					result.put("message", "없는글 또는 삭제된 글 입니다.");
					return result.toString();
				} else {

					result.put("result", 200);
					result.put("message", "모집게시판 글 조회 완료.");
					result.put("board", board);
					return result.toString();
				}
			} catch (Exception e) {
				// TODO: handle exception
				result.put("result", 409);
				result.put("message", "DB조회중 오류가 발생했습니다.");
				return result.toString();
			}
			// 자유 게시판 get
		} else if (board_case.equals("free")) {

			try {
				List<CL_Board> board = new ArrayList<>();

				CL_Board temp_board = appService.getBoard(board_num);

				if (temp_board.getHidden_name().equals("Y")) {
					temp_board.setWrite_user(temp_board.getWrite_user() + "," + "익명");
				} else if (!temp_board.getWrite_user().equals("(알수없음)")) {
					CL_User temp_user = appService.getUser(temp_board.getWrite_user());
					temp_board.setWrite_user(temp_board.getWrite_user() + "," + temp_user.getName());
				} else {
					temp_board.setWrite_user("(알수없음)" + "," + "(알수없음)");
				}

				board.add(temp_board);

				// MM/dd HH:mm 형식으로 포맷 변경된 create_date 업데이트
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
				for (CL_Board clBoard : board) {
					Date create_date = clBoard.getCreate_date();
					String formattedDate = dateFormat.format(create_date);
					clBoard.setFormat_date(formattedDate);
				}

				if (board.get(0) == null) {
					result.put("result", 406);
					result.put("alert", "Y");
					result.put("message", "없는글 또는 삭제된 글 입니다.");
					return result.toString();
				} else {
					result.put("result", 200);
					result.put("message", "자유게시판 글 조회 완료.");
					result.put("board", board);
					return result.toString();
				}
			} catch (Exception e) {
				// TODO: handle exception
				result.put("result", 409);
				result.put("message", "DB조회중 오류가 발생했습니다.");
				return result.toString();
			}
		} else {
			result.put("result", 409);
			result.put("message", "잘못된 접근입니다.");
			return result.toString();
		}

	}

	// 게시글 작성 */
	@PostMapping(value = "board", produces = "application/json; charset=utf8")
	public String InsertBoard(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		// 값이 비었는지 확인
		String[] checkValue = { "accToken", "title", "category", "contents", "hidden_name", "board_case" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true"))
			return check;

		// 유효 길이 체크
		String[] lengthValue = { "title", "contents" };
		int[] checkLength = { 50, 5000 };
		check = checkLength(lengthValue, checkLength, form);
		if (!check.equals("true"))
			return check;

		String accToken = cleanXSS((String) form.get("accToken"));
		String user_id = null;
		String title = cleanXSS((String) form.get("title"));
		String category = cleanXSS((String) form.get("category"));
		String contents = cleanXSS((String) form.get("contents"));
		String hidden_name = cleanXSS((String) form.get("hidden_name"));
		String school = null;
		String board_case = cleanXSS((String) form.get("board_case"));

		// 유효성검사 익명여부 , 카테고리
		check = checkHiddenName(hidden_name);
		if (!check.equals("true"))
			return check;
		check = checkCategory(category, board_case);
		if (!check.equals("true"))
			return check;

		check = checkAccUser(accToken, "Y");
		if (!check.equals("true")) {
			return check;
		} else {
			user_id = jwtProvider.getUsernameFromToken(accToken);
			school = appService.getUser(user_id).getUniv();
		}

		try {

			if (board_case.equals("FREE")) {
				CL_Board board = new CL_Board(user_id, title, category, contents, hidden_name, school, board_case);

				if (appService.insertBoard(board) == 1) {

					// n번 채팅방 소켓 구독자에게 메시지 전송
					CL_Chat message = appService.getLastChat();

					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
					String formattedDate = dateFormat.format(message.getCreate_date());
					message.setFormat_date(formattedDate);

					sendingOperations.convertAndSend("/sub/chat/room/" + user_id, message);

					result.put("result", 200);
					result.put("message", "게시글 작성에 성공했습니다.");
					return result.toString();
				} else {
					result.put("result", 409);
					result.put("message", "게시글 저장중 오류가 발생했습니다.");
					return result.toString();
				}
			} else if (board_case.equals("MATCH")) {

				String[] checkValue_2 = { "gender", "student_id", "delete_date", "max_user" };
				check = checkValue(checkValue_2, form);
				if (!check.equals("true")) {
					return check;
				}
				String location = "";
				if (form.containsKey("location")) {
					location = cleanXSS((String) form.get("location"));
				}
				String gender = cleanXSS((String) form.get("gender"));
				String student_id = cleanXSS((String) form.get("student_id"));
				int max_user = (int) form.get("max_user");

				check = checkGender(gender);
				if (!check.equals("true")) {
					return check;
				}

				check = checkStudentID(student_id);
				if (!check.equals("true")) {
					return check;
				}

				CL_User user = appService.getUser(user_id);
				// 작성자 성별, 조건확인
				check = genderCondition(gender, user.getGender());
				if (!check.equals("true")) {
					result.put("result", 401);
					result.put("alert", "Y");
					result.put("message", "작성자가 조건에 만족하지 않습니다.(성별)");
					return result.toString();
				}
				check = studentIDCondition(student_id, user.getStudent_id());
				if (!check.equals("true")) {
					result.put("result", 401);
					result.put("alert", "Y");
					result.put("message", "작성자가 조건에 만족하지 않습니다.(학번)");
					return result.toString();
				}

				// 포맷터
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // "Asia/Seoul"은 KST 시간대를 나타냅니다.

				Date delete_date = formatter.parse((String) form.get("delete_date"));

				CL_Match_Board board = new CL_Match_Board(user_id, title, category, contents, hidden_name, school,
						board_case, location, gender, student_id, delete_date);
				board.setNum(max_user);

				if (appService.insertMatchBoard(board) == 1) {

					appService.firstChat(user.getName());

					result.put("result", 200);
					result.put("message", "게시글 작성에 성공했습니다.");
					return result.toString();
				} else {
					result.put("result", 409);
					result.put("message", "게시글 저장중 오류가 발생했습니다.");
					return result.toString();
				}
			} else {
				result.put("result", 404);
				result.put("message", "잘못된 접근입니다.");
				return result.toString();
			}
		} catch (Exception e) {

			result.put("result", 409);
			result.put("message", "게시글 작성중 오류가 발생했습니다.");
			return result.toString();
		}
	}

	// 게시글 수정 (제목 내용 카테고리 익명 여부 수정가능)(성별 학번 위치 모집기간 모집인원 수정가능) */
	@PutMapping(value = "board", produces = "application/json; charset=utf8")
	public String updateBoard(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "accToken", "board_num", "title", "category", "contents", "hidden_name" };
		String check = checkValue(checkValue, form);

		if (!check.equals("true")) {
			return check;
		}

		// 유효 길이 체크
		String[] lengthValue = { "title", "contents" };
		int[] checkLength = { 50, 5000 };
		check = checkLength(lengthValue, checkLength, form);
		if (!check.equals("true"))
			return check;

		String accToken = cleanXSS((String) form.get("accToken"));
		int board_num = (int) form.get("board_num");
		String user_id = null;
		String title = cleanXSS((String) form.get("title"));
		String category = cleanXSS((String) form.get("category"));
		String contents = cleanXSS((String) form.get("contents"));
		String hidden_name = cleanXSS((String) form.get("hidden_name"));
		String board_case = cleanXSS((String) form.get("board_case"));

		// 유효성검사 익명여부 , 카테고리
		check = checkHiddenName(hidden_name);
		if (!check.equals("true"))
			return check;
		check = checkCategory(category, board_case);
		if (!check.equals("true"))
			return check;

		// 토큰 유효성 체크
		check = checkAccUser(accToken, "Y");
		if (!check.equals("true")) {
			return check;
		} else {
			user_id = jwtProvider.getUsernameFromToken(accToken);
		}

		try {
			CL_Board check_board = appService.getBoard(board_num);

			if (!check_board.getWrite_user().equals(user_id)) {
				result.put("result", 403);
				result.put("alert", "Y");
				result.put("message", "수정 권한이 없습니다.");
				return result.toString();
			}
			if (!check_board.getBoard_type().equals(board_case)) {
				result.put("result", 401);
				result.put("message", "잘못된 수정 형식입니다.");
				return result.toString();
			}

			if (board_case.equals("FREE")) {
				CL_Board board = new CL_Board();
				board.setNum(board_num);
				board.setWrite_user(user_id);
				board.setTitle(title);
				board.setCategory(category);
				board.setContents(contents);
				board.setHidden_name(hidden_name);

				if (appService.updateBoard(board) == 1) {
					result.put("result", 200);
					result.put("message", "게시글 수정에 성공했습니다.");
					return result.toString();
				} else {
					result.put("result", 409);
					result.put("message", "게시글 수정중 오류가 발생했습니다.");
					return result.toString();
				}
			} else if (board_case.equals("MATCH")) {

				String[] checkValue_2 = { "location", "gender", "student_id", "delete_date", "max_user" };
				check = checkValue(checkValue_2, form);
				if (!check.equals("true")) {
					return check;
				}

				// 포맷터
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // "Asia/Seoul"은 KST 시간대를 나타냅니다.

				String location = (String) form.get("location");
				String gender = (String) form.get("gender");
				String student_id = (String) form.get("student_id");
				Date delete_date = formatter.parse((String) form.get("delete_date"));
				int max_user = (int) form.get("max_user");

				// 작성자 성별, 조건확인
				CL_User user = appService.getUser(user_id);
				check = genderCondition(gender, user.getGender());
				if (!check.equals("true")) {
					result.put("result", 401);
					result.put("alert", "Y");
					result.put("message", "작성자가 조건에 만족하지 않습니다.(성별)");
					return result.toString();
				}
				check = studentIDCondition(student_id, user.getStudent_id());
				if (!check.equals("true")) {
					result.put("result", 401);
					result.put("alert", "Y");
					result.put("message", "작성자가 조건에 만족하지 않습니다.(학번)");
					return result.toString();
				}

				CL_Match_Board board = appService.getMatchBoard(board_num);
				board.setNum(board_num);
				board.setWrite_user(user_id);
				board.setTitle(title);
				board.setCategory(category);
				board.setContents(contents);
				board.setHidden_name(hidden_name);
				board.setLocation(location);
				board.setGender(gender);
				board.setStudent_id(student_id);
				board.setDelete_date(delete_date);

				check = checkGender(gender);
				if (!check.equals("true")) {
					return check;
				}
				check = checkStudentID(student_id);
				if (!check.equals("true")) {
					return check;
				}

				CL_Chat_Room room = new CL_Chat_Room();
				room.setNum(board.getRoom_num());
				room.setRoom_name(title);
				room.setMax_user(max_user);

				if (appService.updateMatchBoard(board, room) == 1) {
					result.put("result", 200);
					result.put("message", "게시글 수정에 성공했습니다.");
					return result.toString();
				} else {

					result.put("result", 409);
					result.put("message", "게시글 수정중 오류가 발생했습니다.");
					return result.toString();
				}
			} else {
				result.put("result", 404);
				result.put("message", "잘못된 접근입니다.");
				return result.toString();

			}

		} catch (Exception e) {
			System.out.println(e);
			result.put("result", 409);
			result.put("message", "게시글 수정중 오류가 발생했습니다.");
			return result.toString();
		}
	}

	// 게시글 삭제 */
	@DeleteMapping(value = "board/{board_case}/{board_num}", produces = "application/json; charset=utf8")
	public String deleteBoard(@RequestParam(defaultValue = "") String accToken,
			@PathVariable("board_case") String board_case, @PathVariable("board_num") int board_num) {

		JSONObject result = new JSONObject();

		// accToken 비어있을 경우 유효성 검사
		if (accToken.equals("")) {
			result.put("result", 400);
			result.put("message", "accessToken 값이 없습니다.");
			return result.toString();
			// 토큰 및 학교인증 여부 확인
		} else {
			String check = checkAccUser(accToken, "Y");
			if (!check.equals("true")) {
				return check;
			}
		}

		String id = jwtProvider.getUsernameFromToken(accToken);
		CL_Board board = appService.getBoard(board_num);
		if (board == null) {
			result.put("result", 406);
			result.put("alert", "Y");
			result.put("message", "없는 게시물 입니다.");
			return result.toString();
		} else if (!board.getWrite_user().equals(id)) {
			result.put("result", 403);
			result.put("alert", "Y");
			result.put("message", "삭제 권한이 없습니다.");
			return result.toString();
		} else if (!board.getBoard_type().equals(board_case.toUpperCase())) {
			result.put("result", 404);
			result.put("message", "잘못된 접근입니다.");
			return result.toString();
		}

		if (board_case.equals("free")) {

			try {

				if (appService.delBoard(board_num) == 1) {
					result.put("result", 200);
					result.put("message", "자유게시판 글 삭제 완료.");

					return result.toString();
				} else {
					result.put("result", 409);
					result.put("message", "삭제중 오류가 발생했습니다.");

					return result.toString();
				}
			} catch (Exception e) {
				// TODO: handle exception
				result.put("result", 409);
				result.put("message", "삭제중 오류가 발생했습니다.");
				return result.toString();
			}

		} else if (board_case.equals("match")) {

			try {

				if (appService.delMatchBoard(board_num) == 1) {
					result.put("result", 200);
					result.put("message", "매치게시판 글 삭제 완료.");

					return result.toString();
				} else {
					result.put("result", 409);
					result.put("message", "삭제중 오류가 발생했습니다.");

					return result.toString();
				}
			} catch (Exception e) {
				// TODO: handle exception
				result.put("result", 409);
				result.put("message", "삭제중 오류가 발생했습니다.");
				return result.toString();
			}
		} else {
			result.put("result", 404);
			result.put("message", "잘못된 접근입니다.");
			return result.toString();
		}
	}

	// 게시글 공감 버튼 처리*/
	@PostMapping(value = "board/reaction", produces = "application/json; charset=utf8")
	public String addReaction(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "accToken", "board_num" };
		String check = checkValue(checkValue, form);

		if (!check.equals("true")) {
			return check;
		}

		String accToken = cleanXSS((String) form.get("accToken"));
		int board_num = (int) form.get("board_num");
		String user_id = null;
		// 토큰 유효성 체크
		check = checkAccUser(accToken, "Y");
		if (!check.equals("true")) {
			return check;
		} else {
			user_id = jwtProvider.getUsernameFromToken(accToken);
		}
		try {

			if (appService.checkReaction(user_id, board_num) >= 1) {
				result.put("result", 401);
				result.put("alert", "Y");
				result.put("message", "이미 공감한 글 입니다.");
				return result.toString();
			}
			if (appService.addReaction(user_id, board_num) == 1) {

				result.put("result", 200);
				result.put("message", "게시글에 공감하였습니다.");
				return result.toString();
			}
			throw new Exception("오류발생");
		} catch (Exception e) {
			result.put("result", 409);
			result.put("message", "게시글 공감중 오류가 발생했습니다.");
			return result.toString();
		}

	}

}
