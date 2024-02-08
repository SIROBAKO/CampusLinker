package com.hako.web.cl.controller.rest;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hako.web.cl.entity.CL_Cert;
import com.hako.web.cl.entity.CL_Univ;
import com.hako.web.cl.entity.CL_User;

@Controller
@RequestMapping({ "/campus-linker/" })
public class CLRestControllerCert extends CLRestController {
	
	
	// 학교인증 비밀번호 인증 */
	@ResponseBody
	@PostMapping(value = { "cert" }, produces = { "application/json; charset=utf8" })
	public String CertSchool(@RequestBody HashMap<String, Object> form) {
		JSONObject result = new JSONObject();

		String[] checkValue = { "purpose" };
		String check = checkValue(checkValue, form);
		if (!check.equals("true")) {
			return check;
		}

		String purpose = (String) form.get("purpose");

		if (purpose.equals("학교인증")) {
			String[] checkValue_school = { "accToken", "email" };
			check = checkValue(checkValue_school, form);
			if (!check.equals("true")) {
				return check;
			}
			String accToken = (String) form.get("accToken");
			String email = (String) form.get("email");
			String id = null;
			String school = null;

			check = checkAccUser(accToken, "N");
			if (!check.equals("true")) {
				return check;
			}
			id = jwtProvider.getUsernameFromToken(accToken);
			school = appService.getUser(id).getUniv();

			check = checkEmail(email);
			if (!check.equals("true")) {
				return check;
			}

			String[] temp = appService.getUniv(school).getMail().split("ac.kr");
			if (!email.contains(temp[0] + "ac.kr")) {
				result.put("result", 403);
				result.put("alert", "Y");
				result.put("message", "이메일 형식이 올바르지 않습니다.");
				return result.toString();
			}
			try {
				if (appService.getUser(id).getCert().equals("Y")) {
					result.put("result", 401);
					result.put("alert", "Y");
					result.put("message", "이미 인증되어있습니다.");
					return result.toString();
				}
				SimpleMailMessage message = new SimpleMailMessage();

				String token = Double.toString(Math.random() * 100000000.0D);

				CL_Cert cert = new CL_Cert(token, id, "학교인증");

				if (appService.insertCert(cert) == 1) {
					message.setTo(email);
					message.setSubject("CampusIt 학교인증");
					message.setText("학교 인증 페이지 입니다.\n\n https://sirobako.co.kr/campus-linker/cert/" + token);

					sender.send(message);

					result.put("result", 200);
					result.put("message", "이메일로 메일을 보냈습니다.");
					return result.toString();
				}

				throw new Exception("DB 저장 실패");
			} catch (Exception e) {
				result.put("result", 409);
				result.put("message", "학교 인증중 오류가 발생했습니다.");

				return result.toString();
			}
		}
		if (purpose.equals("비밀번호")) {
			String[] checkValue_pwd = { "id", "email" };
			check = checkValue(checkValue_pwd, form);
			if (!check.equals("true")) {
				return check;
			}

			String id = (String) form.get("id");
			String email = (String) form.get("email");

			CL_User user = null;
			if ((user = appService.getUser(id)) == null) {
				result.put("result", 401);
				result.put("alert", "Y");
				result.put("message", "해당 아이디가 존재하지 않습니다.");

				return result.toString();
			}

			if (!email.equals(user.getEmail())) {
				result.put("result", 401);
				result.put("alert", "Y");
				result.put("message", "등록된 이메일이 아닙니다.");

				return result.toString();
			}
			try {
				SimpleMailMessage message = new SimpleMailMessage();

				String token = Double.toString(Math.round(Math.random() * 1000000.0D));
				token = token.replace(".0", "");

				CL_Cert cert = new CL_Cert(token, id, "비밀번호");

				if (appService.insertCert(cert) == 1) {
					message.setTo(email);
					message.setSubject("CampusIt 학교인증");
					message.setText("인증 번호 : " + token);

					sender.send(message);

					result.put("result", 200);
					result.put("message", "이메일로 메일을 보냈습니다.");
					return result.toString();
				}

				throw new Exception("DB 저장 실패");
			} catch (Exception e) {
				result.put("result", 409);
				result.put("message", "인증중 오류가 발생했습니다.");

				return result.toString();
			}
		}

		result.put("result", 404);
		result.put("message", "잘못된 접근입니다.");

		return result.toString();
	}

	
	// 확인 페이지 */
	@GetMapping(value = { "cert/{token}" }, produces = { "application/json; charset=utf8" })
	public String CheckCertSchool(HttpServletRequest req,
			@PathVariable(required = false, name = "token") String token) {
		token = token == null ? "" : token;
		try {
			CL_Cert cert = appService.getCert(token);

			Date now = new Date();
			if (cert.getDelete_date().before(now)) {
				appService.delCert(token);

				req.setAttribute("msg", "인증 시간이 초과했습니다.");
				req.setAttribute("url", "back");
				return "alert";
			}

			if (cert.getPurpose().equals("학교인증")) {
				CL_User user = new CL_User();
				user.setId(cert.getUser_id());
				user.setCert("Y");

				if ((appService.updateUser(user) == 1) && (appService.delCert(token) == 1)) {
					req.setAttribute("msg", "인증이 완료되었습니다.");
					req.setAttribute("url", "back");
					return "alert";
				}
				throw new Exception("인증중 오류가 발생하였습니다.");
			}

			req.setAttribute("msg", "잘못된 접근입니다.");
			req.setAttribute("url", "back");
			return "alert";
		} catch (Exception e) {
			req.setAttribute("msg", "오류가 발생했습니다.");
			req.setAttribute("url", "back");
		}
		return "alert";
	}

	// 위치기반 인증 */
	@ResponseBody
	@GetMapping(value = { "cert" }, produces = { "application/json; charset=utf8" })
	public String CheckCertLocation(@RequestParam(defaultValue = "") String accToken,
			@RequestParam(defaultValue = "") String location) {
		JSONObject result = new JSONObject();
		if (accToken.equals("")) {
			result.put("result", 400);
			result.put("message", "accToken값이 없습니다.");
			return result.toString();
		}
		if (location.equals("")) {
			result.put("result", 400);
			result.put("message", "location값이 없습니다.");
			return result.toString();
		}

		String user_id = null;
		String[] locations = location.split(",");
		double location_y = Double.parseDouble(locations[0]);
		double location_x = Double.parseDouble(locations[1]);

		String check = checkAccUser(accToken, "N");
		if (!check.equals("true")) {
			return check;
		}
		user_id = jwtProvider.getUsernameFromToken(accToken);
		try {
			CL_User user = appService.getUser(user_id);
			CL_Univ univ = appService.getUniv(user.getUniv());
			String addr = univ.getAddress();
			addr = URLEncoder.encode(addr, "UTF-8");

			if (user.getCert().equals("Y")) {
				result.put("result", 401);
				result.put("alert", "Y");
				result.put("message", "이미 인증된 계정입니다.");
				return result.toString();
			}

			String[] temp = getUnivLocation(addr).split(",");
			String _y = temp[0];
			String _x = temp[1];
			double y = Double.parseDouble(_y);
			double x = Double.parseDouble(_x);

			double dis = distance(y, x, location_y, location_x, "meter");
			if (dis < 300.0D) {
				user.setCert("Y");
				if (appService.updateUser(user) == 1) {
					result.put("result", 200);
					result.put("message", "학교 위치 인증완료");
					return result.toString();
				}
				throw new Exception("인증중 오류 발생");
			}
			result.put("result", 401);
			result.put("alert", "Y");
			result.put("message", "주소상 위치 300m이내에 있어야합니다.\n현재 위치 차이 : " + (int) dis + "m");
			return result.toString();
		} catch (Exception e) {
			result.put("result", 409);
			result.put("message", "학교인증 중 오류 발생");
		}
		return result.toString();
	}
}