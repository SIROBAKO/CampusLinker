package com.hako.web.cl.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hako.web.cl.entity.CL_Socal;
import com.hako.web.cl.entity.CL_User;

@RestController
@RequestMapping("/campus-linker/")
public class CLRestControllerSocal extends CLRestController {

	// 친구 목록 반환 요청 리스트 + 목록 리스트 *
	@GetMapping(value = "socal/{accToken}", produces = "application/json; charset=utf8")
	public String GetBoardList(@PathVariable(required = false, name = "accToken") String accToken) {
		JSONObject result = new JSONObject();

		// accToken 비어있을경우 반환
		if (accToken.equals("") || accToken == null) {
			result.put("result", 400);
			result.put("message", "accessToken 값이 없습니다.");
			return result.toString();
			// 토큰 유효성 체크 및 학교인증 여부 확인
		} else {
			String check = checkAccUser(accToken, "Y");
			if (!check.equals("true")) {
				return check;
			}
		}
		try {
		List<CL_Socal> socal = appService.getSocalList(jwtProvider.getUsernameFromToken(accToken));
		List<CL_Socal> req_socal = new ArrayList<>();
		List<CL_Socal> acc_socal = new ArrayList<>();

		for (CL_Socal cl_Socal : socal) {
			CL_User user = appService.getUser(cl_Socal.getFriend_id());
			if (user != null) {
				cl_Socal.setUser_id(null);
				cl_Socal.setFriend_id(user.getName());
				// 요청온 상태
				if (cl_Socal.getState().equals("N")) {
					req_socal.add(cl_Socal);
					// 친구 상태
				} else if (cl_Socal.getState().equals("Y")) {
					acc_socal.add(cl_Socal);
				}
			}
		}
		result.put("result", 200);
		result.put("message", "친구 목록 조회 완료.");
		result.put("req_socal", req_socal);
		result.put("acc_socal", acc_socal);
		return result.toString();

		} catch (Exception e) {
			// TODO: handle exception
			result.put("result", 409);
			result.put("message", "친구 목록 조회중 오류가 발생했습니다.");
			return result.toString();
		}

	}

	// 친구 요청 게시글, 댓글, 모집 채팅 창에서 친구신청 가능 *
	// 알림 추가
	@PostMapping(value = "socal", produces = "application/json; charset=utf8")
	public String InsertBoard(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		// 값이 비었는지 확인
		String[] checkValue = { "accToken", "where", "num" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true"))
			return check;

		String accToken = cleanXSS((String) form.get("accToken"));
		String user_id = null;
		String friend_id = null;
		String where = cleanXSS((String) form.get("where"));
		int num = (int) form.get("num");

		check = checkAccUser(accToken, "Y");
		if (!check.equals("true")) {
			return check;
		} else {
			user_id = jwtProvider.getUsernameFromToken(accToken);
		}

		try {

			if (where.equals("free_board")) {

				if ((friend_id = appService.getBoard(num).getWrite_user()) == null) {
					result.put("result", 404);
					result.put("message", "해당 유저가 존재하지 않습니다.");
					return result.toString();
				}

			} else if (where.equals("match_board")) {

				if ((friend_id = appService.getMatchBoard(num).getWrite_user()) == null) {
					result.put("result", 404);
					result.put("message", "해당 유저가 존재하지 않습니다.");
					return result.toString();
				}

			} else if (where.equals("comment")) {
				if ((friend_id = appService.getComment(num).getUser_id()) == null) {
					result.put("result", 404);
					result.put("message", "해당 유저가 존재하지 않습니다.");
					return result.toString();
				}
			} else if (where.equals("chat_room")) {
				if ((friend_id = appService.getChatUser(num).getUser_id()) == null) {
					result.put("result", 404);
					result.put("message", "해당 유저가 존재하지 않습니다.");
					return result.toString();
				}

			} else {
				result.put("result", 404);
				result.put("message", "잘못된 접근입니다.");
				return result.toString();
			}

			CL_Socal socal_1 = new CL_Socal(0, user_id, friend_id, "N");
			CL_Socal socal_2 = new CL_Socal(0, friend_id, user_id, "N");

			if (appService.getSocal(user_id, friend_id) == null && appService.getSocal(friend_id, user_id) == null) {

				if (appService.insertSocal(socal_1) == 1 && appService.insertSocal(socal_2) == 1) {
					result.put("result", 200);
					result.put("message", "친구 요청에 성공했습니다");
					return result.toString();
				}
				throw new Exception("DB저장중 오류발생");
			} else {
				result.put("result", 401);
				result.put("message", "이미 요청된 신청입니다.(차단, 중복)");
				return result.toString();
			}
		} catch (Exception e) {
			e.getMessage();
			result.put("result", 409);
			result.put("message", "친구 요청중 오류가 발생했습니다.");
			return result.toString();
		}

	}

	// 친구 요청 수락 거부 및 차단 *
	@PutMapping(value = "socal", produces = "application/json; charset=utf8")
	public String updateBoard(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "accToken", "socal_num", "answer" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {
			return check;
		}

		String accToken = (String) form.get("accToken");
		int num = (int) form.get("socal_num");
		String answer = (String) form.get("answer");
		String id = null;

		check = checkAccUser(accToken, "Y");
		if (!check.equals("true")) {
			return check;
		} else {
			id = jwtProvider.getUsernameFromToken(accToken);
		}
		try {

			CL_Socal socal = appService.getSocal(num);
			CL_Socal update_socal_1 = null;
			CL_Socal update_socal_2 = null;

			
			if (!id.equals(socal.getUser_id())) {
				result.put("result", 401);
				result.put("message", "허용되지 않은 사용자 입니다.");
				return result.toString();
				// 차단, 거절 (다시는 신청 못하는 상태)
			} else if (answer.equals("N") || answer.equals("F")) {

				update_socal_1 = new CL_Socal(num, socal.getUser_id(), socal.getFriend_id(), "F");
				update_socal_2 = new CL_Socal(num, socal.getFriend_id(), socal.getUser_id(), "F");

			} else if (answer.equals("Y")) {

				update_socal_1 = new CL_Socal(num, socal.getUser_id(), socal.getFriend_id(), "Y");
				update_socal_2 = new CL_Socal(num, socal.getFriend_id(), socal.getUser_id(), "Y");

			} else {
				result.put("result", 400);
				result.put("message", "answer값이 잘못되었습니다.");
				return result.toString();
			}

			if (appService.updateSocal(update_socal_1) == 1 && appService.updateSocal(update_socal_2) == 1) {
				result.put("result", 200);
				result.put("message", "요청이 처리되었습니다.");
				return result.toString();
			}
			throw new Exception("DB 수정중 오류 발생");
		} catch (Exception e) {
			// TODO: handle exception
			result.put("result", 409);
			result.put("message", "친구 요청 처리중 오류가 발생했습니다.");
			return result.toString();
		}

	}

}
