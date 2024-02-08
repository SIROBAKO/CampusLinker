package com.hako.web.cl.controller.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import com.hako.web.cl.entity.CL_User;
import com.hako.web.cl.service.CLChatService;
import com.hako.web.cl.service.CLService;
import com.hako.web.cl.service.FCMService;
import com.hako.web.jwt.JwtProvider;

public class CLRestController {

	@Autowired
	protected CLChatService chatService;

	@Autowired
	protected CLService appService;

	@Autowired
	protected JwtProvider jwtProvider;

	@Autowired
	protected MailSender sender; // 타입으로 받을 수 있음

	@Autowired
	protected SimpMessageSendingOperations sendingOperations;
	
	@Autowired
	protected FCMService fcmService;

	@Value("${fcm.clientId}")
	protected String clientId;

	@Value("${fcm.clientSecret}")
	protected String clientSecret;

	// 카테고리 허용값
	protected String checkCategory(String category, String board_case) {
		JSONObject result = new JSONObject();
		if (board_case.equals("FREE")) {
			if (category.equals("자유") || category.equals("질문")) {
				return "true";
			} else {

				result.put("result", 400);
				result.put("message", "category 값이 올바르지 않습니다.");
				return result.toString();
			}
		} else if (board_case.equals("MATCH")) {
			if (category.equals("자유") || category.equals("수업") || category.equals("식사") || category.equals("여가")
					|| category.equals("공부")) {
				return "true";
			} else {

				result.put("result", 400);
				result.put("message", "category 값이 올바르지 않습니다.");
				return result.toString();
			}
		} else {
			result.put("result", 400);
			result.put("message", "board_case 값이 올바르지 않습니다.");
			return result.toString();
		}
	}

	// 익명 여부 값 유효성 검사
	protected String checkHiddenName(String hidden) {
		JSONObject result = new JSONObject();
		if (hidden.equals("Y") || hidden.equals("N")) {
			return "true";
		} else {

			result.put("result", 400);
			result.put("message", "hidden_name 값이 올바르지 않습니다.");
			return result.toString();
		}
	}

	// 매치게시판 성별 검사
	protected String checkGender(String gender) {
		JSONObject result = new JSONObject();
		if (gender.equals("여자만") || gender.equals("남자만") || gender.equals("성별무관")) {
			return "true";
		} else {

			result.put("result", 400);
			result.put("message", "gender 값이 올바르지 않습니다.");
			return result.toString();
		}
	}

	// 매치게시판 성별 검사
	protected String checkUserGender(String gender) {
		JSONObject result = new JSONObject();
		if (gender.equals("여자") || gender.equals("남자")) {
			return "true";
		} else {

			result.put("result", 400);
			result.put("message", "gender 값이 올바르지 않습니다.");
			return result.toString();
		}
	}

	// 학번 값 유형 체크
	protected String checkStudentID(String check) {
		JSONObject result = new JSONObject();
		check = check.replace(" ", "");

		if (check.contains("만")) {
			String[] tmp = check.split("만");
			if (tmp.length > 1 || !Pattern.matches("^[0-9]*$", tmp[0])) {
				result.put("result", 400);
				result.put("message", "student_id 값이 올바르지 않습니다.");
				return result.toString();
			}

		} else if (check.contains("이하")) {
			String[] tmp = check.split("이하");
			if (tmp.length > 1 || !Pattern.matches("^[0-9]*$", tmp[0])) {
				result.put("result", 400);
				result.put("message", "student_id 값이 올바르지 않습니다.");
				return result.toString();
			}
		} else if (check.contains("이상")) {
			String[] tmp = check.split("이상");
			if (tmp.length > 1 || !Pattern.matches("^[0-9]*$", tmp[0])) {
				result.put("result", 400);
				result.put("message", "student_id 값이 올바르지 않습니다.");
				return result.toString();
			}
		} else if (check.contains("부터")) {
			String[] tmp = check.split("부터");
			if (tmp.length != 2 || !Pattern.matches("^[0-9]*$", tmp[0]) || !Pattern.matches("^[0-9]*$", tmp[1])) {
				result.put("result", 400);
				result.put("message", "student_id 값이 올바르지 않습니다.");
				return result.toString();
			}
		}
		return "true";

	}

	// 아이디 유효성 검사 등등
	// 8자리 이상
	// 영어 숫자만 가능
	protected String checkId(String id) {
		JSONObject result = new JSONObject();

		if (Pattern.matches("^[a-zA-Z0-9]*$", id) || id.length() >= 8) {
			return "true";
		} else {
			result.put("result", 400);
			result.put("alert", "Y");
			result.put("message", "ID형식이 올바르지 않습니다.");
			return result.toString();
		}
	}

	// 닉네임 검사 한굴,영어, 숫자만 가능
	protected String checkName(String name) {
		JSONObject result = new JSONObject();

		if (Pattern.matches("^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]*$", name)) {
			return "true";
		} else {
			result.put("result", 400);
			result.put("alert", "Y");
			result.put("message", "이름형식이 올바르지 않습니다.");
			return result.toString();
		}
	}

	// 이메일 체크
	protected String checkEmail(String email) {
		JSONObject result = new JSONObject();

		if (Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", email)) {
			return "true";
		} else {
			result.put("result", 400);
			result.put("alert", "Y");
			result.put("message", "Email형식이 올바르지 않습니다 ");
			return result.toString();
		}
	}

	// 허용 길이 체크
	protected String checkLength(String[] checkValue, int[] checkLength, HashMap<String, Object> form) {
		JSONObject result = new JSONObject();
		for (int i = 0; i < checkValue.length; i++) {
			String temp = (String) form.get(checkValue[i]);
			if (temp.length() > checkLength[i]) {
				result.put("result", 400);
				result.put("message", checkValue[i] + " 값이 너무 깁니다.");
				return result.toString();
			}
		}
		return "true";
	}

	// 허용 길이 체크(단일)
	protected String checkLength(String checkValue, int checkLength, HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String temp = (String) form.get(checkValue);
		if (temp.length() > checkLength) {
			result.put("result", 400);
			result.put("message", checkValue + " 값이 너무 깁니다.");
			return result.toString();
		}

		return "true";
	}

	// 객체값들 비었는지 확인
	protected String checkValue(String[] checkValue, HashMap<String, Object> form) {

		JSONObject result = new JSONObject();
		for (int i = 0; i < checkValue.length; i++) {
			if (!form.containsKey(checkValue[i]) || form.get(checkValue[i]).equals("")) {

				result.put("result", 400);
				result.put("message", checkValue[i] + " 값이 없습니다.");
				return result.toString();
			}
		}
		return "true";

	}

	// 객체값들 비었는지 확인 (단일)
	protected String checkValue(String checkValue, HashMap<String, Object> form) {

		JSONObject result = new JSONObject();

		if (!form.containsKey(checkValue) || form.get(checkValue).equals("")) {

			result.put("result", 400);
			result.put("message", checkValue + " 값이 없습니다.");
			return result.toString();
		}

		return "true";

	}

	// 매치 게시글 채팅참가 성별검사
	protected String genderCondition(String condition, String user_gender) {
		JSONObject result = new JSONObject();

		if (condition.equals("여자만")) {
			if (!user_gender.equals("여자")) {
				result.put("result", 403);
				result.put("message", "참가조건에 어긋납니다.");
				return result.toString();
			}

		} else if (condition.equals("남자만")) {
			if (!user_gender.equals("남자")) {
				result.put("result", 403);
				result.put("message", "참가조건에 어긋납니다.");
				return result.toString();
			}

		} else if (condition.equals("성별무관")) {
			return "true";
		} else {
			result.put("result", 404);
			result.put("message", "잘못된 요청입니다.");
			return result.toString();
		}
		return "true";
	}

	// 매치게시글 학번 확인

	protected String studentIDCondition(String condition, int user_student_id) {
		JSONObject result = new JSONObject();

		condition = condition.replace(" ", "");

		if (condition.contains("만")) {
			String[] tmp = condition.split("만");
			if (Integer.parseInt(tmp[0]) == user_student_id) {
				return "true";
			}
		} else if (condition.contains("이상")) {
			String[] tmp = condition.split("이상");
			if (Integer.parseInt(tmp[0]) >= user_student_id) {
				return "true";
			}
		} else if (condition.contains("이하")) {
			String[] tmp = condition.split("이하");
			if (Integer.parseInt(tmp[0]) <= user_student_id) {
				return "true";
			}
		} else if (condition.contains("부터")) {
			String[] tmp = condition.split("부터");
			if (Integer.parseInt(tmp[0]) <= user_student_id && Integer.parseInt(tmp[1]) >= user_student_id) {
				return "true";
			}

		} else {
			result.put("result", 404);
			result.put("message", "잘못된 요청입니다.");
			return result.toString();
		}
		result.put("result", 401);
		result.put("message", "학번 조건에 맞지 않습니다.");
		return result.toString();
	}

	// 토큰 유효성 및 대학인증 여부 확인
	protected String checkAccUser(String accToken, String check_cert) {

		// 토큰이 유효하지 않을경우 만료시간 경과 등등
		CL_User user = new CL_User();
		JSONObject result = new JSONObject();

		// 토큰 유효성 검사
		if (!jwtProvider.validateToken(accToken).equals("valid")) {
			result.put("result", 401);
			result.put("message", "accessToken이 유효하지 않습니다.");
			return result.toString();
		}

		// 유효해야만 id 추출 가능
		String id = jwtProvider.getUsernameFromToken(accToken);

		// 서버에 토큰 정보 아이디의 정보가 없을경우
		if (appService.getToken(id) == null) {
			result.put("result", 401);
			result.put("message", "accessToken값이 서버에 존재하지 않습니다.");
			return result.toString();
			// 서버에 저장된 해당 아이디 토큰과 다를 경우
		} else if (!appService.getToken(id).getAccess_token().equals(accToken)) {
			result.put("result", 401);
			result.put("message", "서버 인증 오류(토큰이 유효하나 서버에 정보가 없습니다.)");
			return result.toString();
			// 토큰 DB에 정보가 있지만 userDB에는 해당 정보가 없을 경우
		} else if ((user = appService.getUser(id)) == null) {
			result.put("result", 401);
			result.put("message", "서버 인증 오류(토큰이 유효하나 서버에 정보가 없습니다.)");
			return result.toString();
			// 대학 인증이 안된 경우
		} else if (check_cert.equals("Y") && user.getCert().equals("N")) {
			result.put("result", 401);
			result.put("alert", "Y");
			result.put("message", "대학 인증이 필요합니다.");
			return result.toString();
		} else {
			return "true";
		}
	}

	// 위도경도로 거리계산
	// 두 지점 간의 거리 계산
	protected double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;

		if (unit == "kilometer") {
			dist = dist * 1.609344;
		} else if (unit == "meter") {
			dist = dist * 1609.344;
		}

		return (dist);
	}

	// {
	// Geocoding 개요에 나와있는 API URL 입력
	protected String getUnivLocation(String addr) {
		try {

			String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr; // JSON

			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			// Geocoding 개요에 나와있는 요청 헤더 입력.
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
			con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

			// 요청 결과 확인. 정상 호출인 경우 200
			int responseCode = con.getResponseCode();

			BufferedReader br;

			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}

			String inputLine;

			StringBuffer response = new StringBuffer();

			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}

			br.close();

			JSONTokener tokener = new JSONTokener(response.toString());
			JSONObject object = new JSONObject(tokener);
			JSONArray arr = object.getJSONArray("addresses");

			JSONObject temp = (JSONObject) arr.get(0);
			String _y = (String) temp.get("y");
			String _x = (String) temp.get("x");
			return _y + "," + _x;
		} catch (Exception e) {
			return "fail";
			// TODO: handle exception
		}
	}

	// This function converts decimal degrees to radians
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	// This function converts radians to decimal degrees
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

	// javascript XSS 방지
	protected String cleanXSS(String value) {
		// You'll need to remove the spaces from the html entities below
		value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
		value = value.replaceAll("'", "& #39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		return value;
	}
}
