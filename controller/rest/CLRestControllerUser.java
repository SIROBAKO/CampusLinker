package com.hako.web.cl.controller.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hako.web.cl.entity.CL_Cert;
import com.hako.web.cl.entity.CL_JwtToken;
import com.hako.web.cl.entity.CL_User;

@RestController
@RequestMapping("/campus-linker/")
public class CLRestControllerUser extends CLRestController {

	// 중복 정보 조회 하기 아이디 닉네임 이메일 */
	@GetMapping(value = "user/form/{check}/{param}", produces = "application/json; charset=utf8")
	public String checkUserForm(@PathVariable(required = false, name = "check") String check,
			@PathVariable(required = false, name = "param") String param) {
		JSONObject result = new JSONObject();

		try {
			if (check.equals("id")) {
				if (appService.checkUserForm("ID", param) > 0) {
					result.put("result", 400);
					result.put("message", "중복된 ID 입니다.");
				} else {
					result.put("result", 200);
					result.put("message", "사용가능한 ID 입니다.");
				}
			} else if (check.equals("name")) {
				if (appService.checkUserForm("NAME", param) > 0) {
					result.put("result", 400);
					result.put("message", "중복된 이름 입니다.");

				} else {
					result.put("result", 200);
					result.put("message", "사용가능한 이름 입니다.");
				}
			} else if (check.equals("email")) {
				if (appService.checkUserForm("EMAIL", param) > 0) {
					result.put("result", 400);
					result.put("message", "가입된 이메일 입니다.");

				} else {
					result.put("result", 200);
					result.put("message", "사용가능한 Email 입니다.");
				}
			}

			return result.toString();
		} catch (Exception e) {
			// TODO: handle exception
			result.put("result", 409);
			result.put("message", "유저정보 조회중 오류가 발생했습니다.");
			return result.toString();
		}

	}

	// 유저 정보 반환*/
	@GetMapping(value = "user/{accToken}", produces = "application/json; charset=utf8")
	public String getUser(@PathVariable(required = false, name = "accToken") String token) {
		JSONObject result = new JSONObject();

		// 유효성 검사
		String check = checkAccUser(token, "N");
		if (!check.equals("true")) {
			return check;
		}

		try {
			List<CL_User> user = new ArrayList<>();
			CL_User _user = appService.getUser(jwtProvider.getUsernameFromToken(token));
			_user.setPwd(null);
			user.add(_user);
			result.put("result", 200);
			result.put("message", "회원정보 조회에 성공했습니다.");
			result.put("user", user);
			return result.toString();
		} catch (Exception e) {
			// TODO: handle exception
			result.put("result", 409);
			result.put("message", "유저정보 조회중 오류가 발생했습니다.");
			return result.toString();
		}

	}

	// id 찾기 이메일로 전송*/
	@GetMapping(value = "user", produces = "application/json; charset=utf8")
	public String findId(@RequestParam(defaultValue = "") String email) {
		JSONObject result = new JSONObject();

		if (email.equals("")) {
			result.put("result", 400);
			result.put("message", "email 값이 없습니다.");
			return result.toString();
		}

		String check = checkEmail(email);
		if (!check.equals("true")) {
			return check;
		}
		try {

			SimpleMailMessage message = new SimpleMailMessage();
			String id = appService.findId(email);

			message.setTo(email); // 수신자 설정
			message.setSubject("Campus-Linker 아이디 찾기"); // 메일 제목 설정
			message.setText("Campus-Linker 아이디 : " + id);

			sender.send(message);

			result.put("result", 200);
			result.put("message", "이메일로 메일을 보냈습니다.");
			return result.toString();

		} catch (Exception e) {
			result.put("result", 409);
			result.put("message", "아이디 찾기중 오류가 발생했습니다.");

			return result.toString();
		}

	}

	// 회원가입*/
	@PostMapping(value = "user", produces = "application/json; charset=utf8")
	public String Join(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "school_name", "student_id", "gender", "user_name", "user_id", "user_pwd", "email" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {
			return check;
		}

		// 유효 길이 체크
		String[] lengthValue = { "user_name", "user_id", "user_pwd" };
		int[] checkLength = { 30, 30, 30 };
		check = checkLength(lengthValue, checkLength, form);
		if (!check.equals("true"))
			return check;

		String school_name = cleanXSS((String) form.get("school_name"));
		int student_id = (int) form.get("student_id");
		String gender = cleanXSS((String) form.get("gender"));
		String user_name = cleanXSS((String) form.get("user_name"));
		String user_id = cleanXSS((String) form.get("user_id")).trim();
		String user_pwd = BCrypt.hashpw((String) form.get("user_pwd"), BCrypt.gensalt());
		String email = cleanXSS((String) form.get("email"));

		// id name 유효성 검사 성별 검사
		check = checkId(user_id);
		if (!check.equals("true"))
			return check;
		check = checkName(user_name);
		if (!check.equals("true"))
			return check;
		check = checkUserGender(gender);
		if (!check.equals("true"))
			return check;
		check = checkEmail(email);
		if (!check.equals("true")) {
			return check;
		}

		try {
			CL_User user = new CL_User(0, user_name, school_name, student_id, gender, user_id, user_pwd, email, "N");

			if (appService.checkUserForm("ID", user_id) > 0) {
				result.put("result", 400);
				result.put("alert", "Y");
				result.put("message", user_id + "는 중복된 아이디 입니다.");
			} else if (appService.checkUserForm("NAME", user_name) > 0) {
				result.put("result", 400);
				result.put("alert", "Y");
				result.put("message", user_name + "는 중복된 닉네임 입니다.");
			} else if (appService.checkUserForm("EMAIL", email) > 0) {
				result.put("result", 400);
				result.put("alert", "Y");
				result.put("message", email + "는 가입된 이메일 입니다.");
			} else if (appService.joinUesr(user) == 1) {
				result.put("result", 200);
				result.put("message", "회원가입에 성공했습니다.");
			} else {
				throw new Exception("DB 저장 실패");
			}
			return result.toString();
		} catch (Exception e) {
			// TODO: handle exception

			result.put("result", 409);
			result.put("message", "회원가입중 오류가 발생했습니다.");

			return result.toString();
		}

	}

	// 회원 정보 수정 닉네임 비밀번호 이메일 변경 + 비밀번호 찾기 재설정도 포함시켰음 * /
	@PutMapping(value = "user", produces = "application/json; charset=utf8")
	public String UpdateUser(@RequestBody HashMap<String, Object> form) {

		JSONObject result = new JSONObject();

		String check = checkValue("purpose", form);
		if (!check.equals("true")) {
			return check;
		}

		String purpose = (String) form.get("purpose");
		String accToken = null;
		String id = null;

		CL_User user = new CL_User();

		// 재설정이 아닐경우
		if (!purpose.equals("reset")) {
			// 비어있는지 검사
			check = checkValue("accToken", form);
			if (!check.equals("true")) {
				return check;
			}

			accToken = (String) form.get("accToken");

			// 유효성 검사
			check = checkAccUser(accToken, "N");
			if (!check.equals("true")) {
				return check;
			}

			// id값 추출
			id = jwtProvider.getUsernameFromToken(accToken);
			user.setId(id);
		}

		if (purpose.equals("")) {
			result.put("result", 401);
			result.put("message", "purpose 값이 없습니다.");
			return result.toString();
		}
//			이름 재설정
		if (purpose.equals("name")) {
			// 이름 유효성검사
			check = checkValue("name", form);
			if (!check.equals("true")) {
				return check;
			}
			check = checkLength("name", 30, form);
			if (!check.equals("true"))
				return check;

			String name = cleanXSS((String) form.get("name"));
			user.setName(name);

			check = checkName(name);
			if (!check.equals("true"))
				return check;

//			이메일 재설정
		} else if (purpose.equals("email")) {
			// 이메일 유효성검사
			check = checkValue("email", form);
			if (!check.equals("true")) {
				return check;
			}

			String email = cleanXSS((String) form.get("email"));

			check = checkEmail(email);
			if (!check.equals("true")) {
				return check;
			}
			user.setEmail(email);

//			비밀번호 재설정
		} else if (purpose.equals("pwd")) {

			check = checkValue("pwd", form);
			if (!check.equals("true")) {
				return check;
			}
			check = checkLength("pwd", 30, form);
			if (!check.equals("true"))
				return check;

			String pwd = BCrypt.hashpw((String) form.get("pwd"), BCrypt.gensalt());
			user.setPwd(pwd);

//			비밀번호 재설정
		} else if (purpose.equals("reset")) {
			String[] checkValue = { "id", "token", "pwd" };
			check = checkValue(checkValue, form);
			if (!check.equals("true")) {
				return check;
			}

			String token = (String) form.get("token");
			String pwd = (String) form.get("pwd");

			CL_Cert cert = appService.getCert(token);

			Date now = new Date();
			if (cert.getDelete_date().before(now)) {
				appService.delCert(token);
				result.put("result", 401);
				result.put("message", "인증시간이 지났습니다.");
				return result.toString();
				// 토큰 삭제 성공하면
			} else if (cert.getPurpose().equals("비밀번호")) {
				if (appService.delCert(token) == 1) {
					user.setId((String) form.get("id"));
				}
			}

			check = checkLength("pwd", 30, form);
			if (!check.equals("true"))
				return check;

			pwd = BCrypt.hashpw((String) form.get("pwd"), BCrypt.gensalt());
			user.setPwd(pwd);

//		잘못된 접근
		} else {
			result.put("result", 404);
			result.put("message", "잘못된 접근입니다.");
			return result.toString();
		}

		try {
			if (appService.updateUser(user) == 1) {
				result.put("result", 200);
				result.put("message", "회원 정보 수정에 성공하였습니다.");
				return result.toString();
			}

			throw new Exception("DB 저장 실패");
		} catch (Exception e) {
			// TODO: handle exception
			result.put("result", 409);
			result.put("message", "회원 정보 수정중 오류가 발생하였습니다..");
			return result.toString();
		}

	}

	// 회원 탈퇴 */
	@DeleteMapping(value = { "user/{accToken}", "user" }, produces = "application/json; charset=utf8")
	public String DeleteUser(@PathVariable(required = false, name = "accToken") String token,
			@RequestParam(defaultValue = "") String pwd) {
		JSONObject result = new JSONObject();

		if (token == null || token.equals("")) {
			result.put("result", 400);
			result.put("message", "accToken 값이 없습니다.");
			return result.toString();
		} else if (pwd.equals("")) {
			result.put("result", 400);
			result.put("message", "pwd 값이 없습니다.");
			return result.toString();
		} else {

			String check = checkAccUser(token, "N");
			if (!check.equals("true")) {
				return check;
			}
		}

		try {
			// 비밀번호 적합반환
			String id = jwtProvider.getUsernameFromToken(token);
			String _pwd = appService.getUser(id).getPwd();
			if (!BCrypt.checkpw(pwd, _pwd)) {
				result.put("result", 401);
				result.put("alert", "Y");
				result.put("message", "비밀번호가 틀립니다.");
				return result.toString();
			} else {

				if (appService.delUesr(id) == 1) {
					result.put("result", 200);
					result.put("message", "회원 탈퇴에 성공했습니다.");
					return result.toString();
				}

				result.put("result", 409);
				result.put("message", "회원 탈퇴중 오류가 발생하였습니다.");
				return result.toString();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			result.put("result", 409);
			result.put("message", "회원 탈퇴중 오류가 발생하였습니다.");
			return result.toString();
		}
	}

	// 토큰생성 */
	@PostMapping(value = "token", produces = "application/json; charset=utf8")
	public String LoginToken(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "id", "pwd" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {

			return check;
		}

		try {
			String id = ((String) form.get("id")).trim();
			String pwd = (String) form.get("pwd");

			// 테스트 아이디 생성
			if (pwd.equals("1234")) {

				// 해당 아이디 유저 없을때
				if (appService.getUser(id) == null) {
					// 테스트 아이디 생성 및 로그인 완료

					CL_User user = new CL_User(0, id+"(테스트)", "한신대학교", 18, "남자", id, BCrypt.hashpw("1234", BCrypt.gensalt()),
							"test_" + id + "@hs.ac.kr", "Y");

					if (appService.checkUserForm("ID", id) > 0) {
						result.put("result", 400);
						result.put("alert", "Y");
						result.put("message", id + "는 중복된 아이디 입니다.");

						return result.toString();
					} else if (appService.joinUesr(user) == 0) {
						result.put("result", 405);
						result.put("message", "테스트 가입에 실패했습니다.");
						return result.toString();
					}
					// id로부터 토큰 생산
					Map<String, String> jwtToken = jwtProvider.generateTokenSet(id);

					// 이미 존재하면 삭제
					if (appService.getToken(id) != null) {
						appService.deleteToken(id);
					}

					// 새로저장
					// 데이터 베이스 저장
					CL_JwtToken token = new CL_JwtToken(0, id, jwtToken.get("accessToken"),
							jwtToken.get("refreshToken"), null);
					if (appService.insertToken(token) == 1) {
						result.put("result", 200);
						result.put("message", "토큰 생성에 성공했습니다.");
						result.put("accessToken", jwtToken.get("accessToken"));
						result.put("refreshToken", jwtToken.get("refreshToken"));
						return result.toString();
					}

					result.put("result", 409);
					result.put("message", "토큰 저장중 오류가 발생하였습니다.");
					return result.toString();

				}

			}

			// 서버에 저장된 비밀번호 가져와서 비교
			// 아이디에 해당하는 정보가 없으면 반환
			if (appService.getUser(id) == null) {
				System.out.println(pwd);
				result.put("result", 401);
				result.put("alert", "Y");
				result.put("message", "아이디또는 비밀번호가 틀립니다.");

				return result.toString();
			}

			// 비밀번호 적합반환
			String _pwd = appService.getUser(id).getPwd();
			if (!BCrypt.checkpw(pwd, _pwd)) {
				result.put("result", 401);
				result.put("alert", "Y");
				result.put("message", "아이디또는 비밀번호가 틀립니다.");
				return result.toString();
			} else {

				// id로부터 토큰 생산
				Map<String, String> jwtToken = jwtProvider.generateTokenSet(id);

				// 이미 존재하면 삭제
				if (appService.getToken(id) != null) {
					appService.deleteToken(id);
				}

				// 새로저장
				// 데이터 베이스 저장
				CL_JwtToken token = new CL_JwtToken(0, id, jwtToken.get("accessToken"), jwtToken.get("refreshToken"),
						null);
				if (appService.insertToken(token) == 1) {
					result.put("result", 200);
					result.put("message", "토큰 생성에 성공했습니다.");
					result.put("accessToken", jwtToken.get("accessToken"));
					result.put("refreshToken", jwtToken.get("refreshToken"));
					return result.toString();
				}

				result.put("result", 409);
				result.put("message", "토큰 저장중 오류가 발생하였습니다.");
				return result.toString();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			result.put("result", 409);
			result.put("message", "토큰 생성중 오류가 발생하였습니다.");
			return result.toString();
		}
	}

	// 토큰 재발급 */
	@PutMapping(value = "token", produces = "application/json; charset=utf8")
	public String CheckToken(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();
		String id = null;
		String _checkToken = null;
		String refToken = (String) form.get("refToken");
		String checkToken = refToken;

		// 유효성 검사
		String[] checkValue = { "refToken" };
		String checkVal = checkValue(checkValue, form);
		if (!checkVal.equals("true")) {
			return checkVal;
		}

		try {
			// refToken 유효할 경우
			if (jwtProvider.validateToken(refToken).equals("valid")) {
				id = jwtProvider.getUsernameFromToken(refToken);
				try {
					// 서버에 저장된 토큰 값 가져오기
					_checkToken = appService.getToken(id).getRefresh_token();
				} catch (Exception e) {
					// 서버에 해당아이디 토큰이 없을 시
					result.put("result", 401);
					result.put("message", "서버에 저장된 토큰값이 아닙니다.");

					return result.toString();
				}
			} else {
				result.put("result", 401);
				result.put("message", "토큰이 만료되었습니다.");
				return result.toString();
			}

			// 토큰은 유효하지만 서버에 저장된 토큰과 다를 때
			if (!checkToken.equals(_checkToken)) {
				result.put("result", 401);
				result.put("message", "서버에 저장된 토큰값이 아닙니다.");

				return result.toString();
			}

			// 서버와 토큰값이 같고 유효할때
			// 토큰 새로 생성

			Map<String, String> jwtToken = jwtProvider.generateTokenSet(id);
			CL_JwtToken token = new CL_JwtToken(0, id, jwtToken.get("accessToken"), jwtToken.get("refreshToken"), null);
			if (appService.updateToken(token) == 1) {
				result.put("result", 200);
				result.put("message", "인증이 완료되었습니다.");
				result.put("accessToken", jwtToken.get("accessToken"));
				result.put("refreshToken", jwtToken.get("refreshToken"));

				return result.toString();
			} else {
				result.put("result", 409);
				result.put("message", "토큰 저장중 오류가 발생하였습니다.");
				return result.toString();
			}

		} catch (Exception e) {
			// TODO: handle exception
			result.put("result", 409);
			result.put("message", "토큰 조회중 오류가 발생하였습니다.");
			return result.toString();

		}
	}

	// 토큰 삭제 로그아웃 */
	@DeleteMapping(value = { "token", "token/{accToken}" }, produces = "application/json; charset=utf8")
	public String DeleteToken(@PathVariable(required = false, name = "accToken") String token) {
		JSONObject result = new JSONObject();

		// 유효성 검사
		if (token == null || token.equals("")) {
			result.put("result", 400);
			result.put("message", "accToken 값이 없습니다.");
			return result.toString();
		} else {
			String check = checkAccUser(token, "N");
			if (!check.equals("true")) {
				return check;
			}
		}

		try {
			appService.deleteToken(jwtProvider.getUsernameFromToken(token));
			result.put("result", 200);
			result.put("message", "로그아웃에 성공했습니다.");
			return result.toString();
		} catch (Exception e) {
			// TODO: handle exception
			result.put("result", 409);
			result.put("message", "로그아웃중 오류가 발생했습니다.");
			return result.toString();
		}

	}

}
