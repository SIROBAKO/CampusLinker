package com.hako.web.cl.controller.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hako.web.cl.dto.CLMatchBoardDTO;
import com.hako.web.cl.entity.CL_Alarm;
import com.hako.web.cl.entity.CL_Board;
import com.hako.web.cl.entity.CL_Chat;
import com.hako.web.cl.entity.CL_Chat_User;
import com.hako.web.cl.entity.CL_Comment;
import com.hako.web.cl.entity.CL_JwtToken;
import com.hako.web.cl.entity.CL_Match_Board;
import com.hako.web.cl.entity.CL_Report;
import com.hako.web.cl.entity.CL_Univ;

@RestController
@RequestMapping({ "/campus-linker/" })
public class CLRestControllerOther extends CLRestController {

	// 학교명 조회 */
	@GetMapping(value = { "school/{school_name}", "school" }, produces = { "application/json; charset=utf8" })
	public String School(@PathVariable(required = false, name = "school_name") String school_name) {
		JSONObject result = new JSONObject();

		if ((school_name == null) || (school_name.equals(""))) {
			result.put("result", 400);
			result.put("message", "학교명이 비어있습니다.");
			return result.toString();
		}
		try {
			List<CL_Univ> list = appService.getUnivList(school_name);
			for (CL_Univ CL_Univ : list) {
				String[] temp = CL_Univ.getMail().split("ac.kr");
				CL_Univ.setMail(temp[0] + "ac.kr");
			}
			if (list.get(0) == null) {
				result.put("result", 406);
				result.put("alert", "Y");
				result.put("message", "조회 값이 없습니다..");

				return result.toString();
			}
			result.put("result", 200);
			result.put("message", "학교조회에 성공하였습니다.");
			result.put("school_list", list);

			return result.toString();
		} catch (Exception e) {
			result.put("result", 409);
			result.put("message", "조회중 오류가 발생하였습니다.");
		}
		return result.toString();
	}

	@PostMapping(value = { "report" }, produces = { "application/json; charset=utf8" })
	public String reportUser(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "accToken", "reason", "purpose", "num" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {
			return check;
		}

		String accToken = (String) form.get("accToken");
		String reason = (String) form.get("reason");
		String purpose = (String) form.get("purpose");
		int num = ((Integer) form.get("num")).intValue();

		check = checkAccUser(accToken, "N");
		if (!check.equals("true")) {
			return check;
		}
		CL_Report report = new CL_Report();
		report.setPurpose(purpose);
		report.setPurpose_num(num);
		try {
			if (purpose.equals("board_free")) {
				CL_Board board = appService.getBoard(num);
				report.setReport_user(board.getWrite_user());

				String data = "report 유저 : " + board.getWrite_user() + "\n\n사유 : " + reason + "\n\n제목 : "
						+ board.getTitle() + "\n\n게시글 내용 : " + board.getContents();
				report.setPurpose_data(data);
			} else if (purpose.equals("board_match")) {
				CL_Match_Board board = appService.getMatchBoard(num);
				report.setReport_user(board.getWrite_user());
				String data = "report 유저 : " + board.getWrite_user() + "\n\n사유 : " + reason + "\n\n제목 : "
						+ board.getTitle() + "\n\n게시글 내용 : " + board.getContents();
				report.setPurpose_data(data);
			} else if (purpose.equals("comment")) {
				CL_Comment comment = appService.getComment(num);
				report.setReport_user(comment.getUser_id());
				String data = "report 유저 : " + comment.getUser_id() + "\n\n사유 : " + reason + "\n\n댓글 : "
						+ comment.getComment();

				report.setPurpose_data(data);
			} else if (purpose.equals("chat")) {
				CL_Chat_User chat_user = appService.getChatUser(num);
				report.setReport_user(chat_user.getUser_id());

				String data = "report 유저 : " + chat_user.getUser_id() + "\n\n사유 : " + reason + "\n\n";

				for (int i = 3; i >= 0; i--) {
					List<CL_Chat> chat_list = appService.getChatList(chat_user.getRoom_num(), i);

					for (CL_Chat cl_Chat : chat_list) {
						data = data + "채팅 유저 : " + cl_Chat.getUser_id() + "\n" + "내용 : " + cl_Chat.getChat() + "\n\n";
					}
				}
				report.setPurpose_data(data);
			} else {
				result.put("result", 400);
				result.put("message", "purpose 값이 올바르지 않습니다.");
				return result.toString();
			}

			if (appService.insertReport(report) == 1) {
				result.put("result", 200);
				result.put("message", "신고 처리 되었습니다.");
				return result.toString();
			}
			throw new Exception("DB저장중 문제 발생");
		} catch (Exception e) {
			result.put("result", 409);
			result.put("message", "신고처리중 오류가 발생했습니다.");
		}
		return result.toString();
	}

	// 알람 조회
	@GetMapping(value = { "alarm" }, produces = { "application/json; charset=utf8" })
	public String Alarm(@RequestParam(defaultValue = "") String accToken) {
		JSONObject result = new JSONObject();

		// accToken 비어있을경우 반환
		if (accToken.equals("")) {
			result.put("result", 400);
			result.put("message", "accessToken 값이 없습니다.");
			return result.toString();
			// 토큰 유효성 체크
		} else {
			String check = checkAccUser(accToken, "N");
			if (!check.equals("true")) {
				return check;
			}
		}

		try {
			String user_id = jwtProvider.getUsernameFromToken(accToken);

			List<CL_Alarm> list = appService.getAlarm(user_id);

			// MM/dd HH:mm 형식으로 포맷 변경된 create_date 업데이트
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");

			for (CL_Alarm cl_Alarm : list) {
				Date create_date = cl_Alarm.getCreate_date();
				String formatDate = dateFormat.format(create_date);
				cl_Alarm.setFormat_date(formatDate);

				if (cl_Alarm.getPurpose().equals("match")) {
					if (appService.getMatchBoard(cl_Alarm.getPurpose_num()) != null) {
						cl_Alarm.setPurpose_title(appService.getMatchBoard(cl_Alarm.getPurpose_num()).getTitle());
					}else {
						cl_Alarm.setPurpose_title("삭제된 게시물 입니다.");
					}
				} else if (cl_Alarm.getPurpose().equals("free")) {
					if (appService.getBoard(cl_Alarm.getPurpose_num())!= null) {
						cl_Alarm.setPurpose_title(appService.getBoard(cl_Alarm.getPurpose_num()).getTitle());
					}else {
						cl_Alarm.setPurpose_title("삭제된 게시물 입니다.");
					}
				} else {
					if (appService.getRoomName(cl_Alarm.getPurpose_num()) != null) {
						cl_Alarm.setPurpose_title(appService.getRoomName(cl_Alarm.getPurpose_num()));
					}else {
						cl_Alarm.setPurpose_title("삭제된 채팅방 입니다.");
					}
				}
			}

			result.put("result", 200);
			result.put("message", "알람조회에 성공하였습니다.");
			result.put("school_list", list);

			return result.toString();
		} catch (Exception e) {
			System.out.println(e);
			result.put("result", 409);
			result.put("message", "조회중 오류가 발생하였습니다.");
		}
		return result.toString();
	}

	// 디바이스 토큰 받기

	@PostMapping(value = { "fcm/token" }, produces = { "application/json; charset=utf8" })
	public String setDivceToken(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "accToken", "deviceToken" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {
			return check;
		}

		String accToken = (String) form.get("accToken");
		String deviceToken = (String) form.get("deviceToken");

		check = checkAccUser(accToken, "N");
		if (!check.equals("true")) {
			return check;
		}

		String user_id = jwtProvider.getUsernameFromToken(accToken);
		try {

			CL_JwtToken token = new CL_JwtToken(0, user_id, null, null, deviceToken);
			if (appService.setDeviceToken(token) == 1) {
				result.put("result", 200);
				result.put("message", "디바이스 토큰 저장에 성공했습니다.");

				return result.toString();
			} else {
				throw new Exception("저장 실패");
			}
		} catch (Exception e) {
			System.out.println(e);
			result.put("result", 409);
			result.put("message", "디바이스 토큰 저장중 오류가 발생했습니다.");
		}
		return result.toString();

	}

}