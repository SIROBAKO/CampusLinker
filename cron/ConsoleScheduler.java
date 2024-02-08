package com.hako.web.cl.cron;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hako.web.cl.dto.CLChatRoomInfo;
import com.hako.web.cl.entity.CL_Match_Board;
import com.hako.web.cl.service.CLService;

@Component
public class ConsoleScheduler {

	@Autowired
	CLService appService;

	@Scheduled(cron = "0 * * * * *")
	public void delMatch_Board() {
		List<CL_Match_Board> board ;

		Date now = new Date();
		if ((board = appService.getBoardListAll()) != null) {
			for (CL_Match_Board cl_Match_Board : board) {
				if (cl_Match_Board.getDelete_date().before(now))
					appService.delMatchBoard(cl_Match_Board.getNum());

				List<CLChatRoomInfo> info = appService.getChatRoomInfo(cl_Match_Board.getRoom_num());

				for (CLChatRoomInfo userNum : info) {

					if (userNum.getUserCount() == 0) {
						appService.delMatchBoard(cl_Match_Board.getNum());
					}
				}

			}
		}
	}
}
