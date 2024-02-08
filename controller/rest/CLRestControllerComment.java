package com.hako.web.cl.controller.rest;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hako.web.cl.dto.FCMNotificationRequestDto;
import com.hako.web.cl.entity.CL_Alarm;
import com.hako.web.cl.entity.CL_Board;
import com.hako.web.cl.entity.CL_Comment;

@RestController
@RequestMapping("/campus-linker/")
public class CLRestControllerComment extends CLRestController {

	// 댓글 목록 조회 */
	@GetMapping(value = { "comment/{board_num}/{accToken}", "comment/{board_num}",
			"comment" }, produces = "application/json; charset=utf8")
	public String getCommentList(@PathVariable(required = false, name = "accToken") String accToken,
			@PathVariable(required = false, name = "board_num") String _board_num) {
		JSONObject result = new JSONObject();

		if (_board_num == null || _board_num.equals("")) {
			result.put("result", 400);
			result.put("message", "board_num 값이 없습니다.");
			return result.toString();
		} else if (accToken == null || accToken.equals("")) {
			result.put("result", 400);
			result.put("message", "accessToken 값이 없습니다.");
			return result.toString();
		}

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

		int board_num = Integer.parseInt(_board_num);

		try {

			try {
				List<CL_Comment> comment = appService.getCommentList(board_num);
				CL_Board board = appService.getBoard(board_num);
				int i = 1;
				HashMap<String, String> hidden = new HashMap<String, String>();
				String user_name = null;

				// MM/dd HH:mm 형식으로 포맷 변경된 create_date 업데이트
				SimpleDateFormat dateFormat_create_date = new SimpleDateFormat("MM/dd HH:mm");

				for (CL_Comment cl_Comment : comment) {

					Date create_date = cl_Comment.getCreate_date();
					String formattedCreateDate = dateFormat_create_date.format(create_date);
					cl_Comment.setFormat_date(formattedCreateDate);

					if (cl_Comment.getHidden_name().equals("Y")) {

						// 임시 저장된 유저데이터 중에 있으면 해당 이름 적용
						for (int x = 1; x < hidden.size() + 1; x++) {
							String temp = "익명" + x;

							if (hidden.get(temp).equals(cl_Comment.getUser_id())) {
								user_name = "익명" + x;
								break;
							}
						}

						// 없으면 임시저장후 이름적용
						if (user_name == null) {
							user_name = "익명" + i;
							hidden.put(user_name, cl_Comment.getUser_id());
							i++;
						}

						if (board.getWrite_user().equals(cl_Comment.getUser_id())) {
							user_name += "(글쓴이)";
						}
						cl_Comment.setUser_id(cl_Comment.getUser_id() +","+user_name);
						user_name = null;

					} else if (cl_Comment.getUser_id().equals("(알수없음)")) {
						continue;
					} else {
						if (board.getWrite_user().equals(cl_Comment.getUser_id())) {
							cl_Comment.setUser_id(cl_Comment.getUser_id() + "," +  appService.getUser(cl_Comment.getUser_id()).getName() + "(글쓴이)" );
						} else {

							cl_Comment.setUser_id(cl_Comment.getUser_id() + "," + appService.getUser( cl_Comment.getUser_id()).getName() );
						}
					}

				}

				result.put("result", 200);
				result.put("message", "댓글 목록 조회 완료.");
				result.put("revers_list", comment);
				Collections.reverse(comment);
				result.put("list", comment);
				return result.toString();
			} catch (Exception e) {
				// TODO: handle exception
				result.put("result", 409);
				result.put("message", "DB조회중 오류가 발생했습니다.");
				return result.toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
			result.put("result", 409);
			result.put("message", "잘못된 접근입니다.");
			return result.toString();
		}

	}

	// 댓글작성 */
	// 알림기능 넣기 글작성자에게 알림, 대댓글일시 대댓글 알림 유저에게 O
	@PostMapping(value = "comment", produces = "application/json; charset=utf8")
	public String insertComment(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "accToken", "ref", "ref_comment", "comment", "hidden_name" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {
			return check;
		}

		// 유효 길이 체크
		check = checkLength("comment", 1000, form);
		if (!check.equals("true"))
			return check;

		int ref = (int) form.get("ref");
		int ref_comment = (int) form.get("ref_comment");
		String _comment = cleanXSS((String) form.get("comment"));
		String hidden_name = (String) form.get("hidden_name");
		String accToken = (String) form.get("accToken");
		String id = null;
		// hidden_name check
		check = checkHiddenName(hidden_name);
		if (!check.equals("true"))
			return check;

		check = checkAccUser(accToken, "Y");
		if (!check.equals("true")) {
			return check;
		} else {
			id = jwtProvider.getUsernameFromToken(accToken);

		}

		try {
			CL_Comment comment = new CL_Comment();
			comment.setRef(ref);
			comment.setRef_comment(ref_comment);
			comment.setComment(_comment);
			comment.setUser_id(id);
			comment.setHidden_name(hidden_name);

			if (appService.insertComment(comment) == 1) {

				CL_Board board = appService.getBoard(ref);

				// 게시글 작성자에게만 댓글의 주체가 자기자신일경우 제외
				if (!id.trim().equals(board.getWrite_user().trim())) {
					CL_Alarm alarm = new CL_Alarm();
					alarm.setContent("게시물에 댓글이 달렸습니다.\n\n\"" + _comment + "\"");
					alarm.setPurpose(board.getBoard_type().toLowerCase());
					alarm.setPurpose_num(board.getNum());
					alarm.setUser(board.getWrite_user());
					appService.setAlarm(alarm);
					
					// FCM 추가부분

					String deviceToken = appService.getDeviceToken(board.getWrite_user());

					FCMNotificationRequestDto notificationRequest = new FCMNotificationRequestDto();
					notificationRequest.setDeviceToken(deviceToken);
					notificationRequest.setTitle("댓글이 작성되었습니다.");
					notificationRequest.setBody(_comment);
					notificationRequest.setPurpose(board.getBoard_type().toLowerCase());
					notificationRequest.setPurpose_num(board.getNum());
			
					
					if (notificationRequest.getPurpose().equals("match")) {
						if (appService.getMatchBoard(notificationRequest.getPurpose_num()) != null) {
							notificationRequest.setPurpose_title(appService.getMatchBoard(notificationRequest.getPurpose_num()).getTitle());
						}else {
							notificationRequest.setPurpose_title("삭제된 게시물 입니다.");
						}
					} else if (notificationRequest.getPurpose().equals("free")) {
						if (appService.getBoard(notificationRequest.getPurpose_num())!= null) {
							notificationRequest.setPurpose_title(appService.getBoard(notificationRequest.getPurpose_num()).getTitle());
						}else {
							notificationRequest.setPurpose_title("삭제된 게시물 입니다.");
						}
					} 
					
					fcmService.sendNotification(notificationRequest);
					
				}

				
				result.put("result", 200);
				result.put("message", "댓글 작성에 성공했습니다.");
				return result.toString();
			}
			throw new Exception("DB 저장 실패");

		} catch (Exception e) {
			// TODO: handle exception

			result.put("result", 409);
			result.put("message", "댓글 작성중 오류가 발생했습니다.");

			return result.toString();
		}
	}

	// 댓글 삭제 */
	@DeleteMapping(value = { "comment/{comment_num}/{accToken}", "comment/{comment_num}",
			"comment" }, produces = "application/json; charset=utf8")
	public String delComment(@PathVariable(required = false, name = "accToken") String accToken,
			@PathVariable(required = false, name = "comment_num") String comment_num) {
		JSONObject result = new JSONObject();

		if (comment_num == null || comment_num.equals("")) {
			result.put("result", 400);
			result.put("message", "comment_num 값이 없습니다.");
			return result.toString();
		} else if (accToken == null || accToken.equals("")) {
			result.put("result", 400);
			result.put("message", "accessToken 값이 없습니다.");
			return result.toString();
		}
		// accToken 비어있을 경우 유효성 검사

		int num = Integer.parseInt(comment_num);

		String check = checkAccUser(accToken, "Y");
		String id = null;
		if (!check.equals("true")) {
			return check;
		} else {
			id = jwtProvider.getUsernameFromToken(accToken);
		}

		if (!appService.getComment(num).getUser_id().equals(id)) {
			result.put("result", 403);
			result.put("message", "삭제 권한이 없습니다.");
			return result.toString();
		}
		try {

			if (appService.delComment(num) != 0) {
				result.put("result", 200);
				result.put("message", "댓글 삭제 완료.");

				return result.toString();
			}
			throw new Exception("DB저장 오류 발생");
		} catch (Exception e) {
			// TODO: handle exception
			result.put("result", 409);
			result.put("message", "삭제중 오류가 발생했습니다.");
			return result.toString();
		}

	}

}
