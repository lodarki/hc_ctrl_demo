package com.ts.hc_ctrl_demo.controller;

import com.ts.hc_ctrl_demo.common.entity.ApiResult;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.NetDvrTimeEx;
import com.ts.hc_ctrl_demo.service.AlarmService;
import com.ts.hc_ctrl_demo.service.CardService;
import com.ts.hc_ctrl_demo.service.ListenService;
import com.ts.hc_ctrl_demo.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping(value = "/hc")
public class HcController {

    private Logger logger = LoggerFactory.getLogger(HcController.class);

    @Resource
    private LoginService loginService;

    @Resource
    private AlarmService alarmService;

    @Resource
    private ListenService listenService;

    @Resource
    private CardService cardService;

    /**
     * 登陆
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login")
    public String hcLogin() {
        if (loginService.login()) {
            return ApiResult.Ok("注册成功！").toJSon();
        }
        return ApiResult.Error(500, "注册失败！").toJSon();
    }

    /**
     * 布防
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/setupAlarm")
    public String setupAlarm() {
        return alarmService.setupAlarmChan(LoginService.lUserID).toJSon();
    }

    /**
     * 开启监听
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listen")
    public String listen() {
        return listenService.startAlarmListen().toJSon();
    }

    /**
     * 关闭监听
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/stopListen")
    public String stopListen() {
        return listenService.stopAlarmListen().toJSon();
    }


    /**
     * 注销
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/exit")
    public String exit() {
        if (loginService.logout()) {
            return ApiResult.Ok("注销成功！").toJSon();
        }
        return ApiResult.Error(500, "注销失败！").toJSon();
    }

    @ResponseBody
    @RequestMapping(value = "/addCard")
    public String addCard(HttpServletRequest request) {
        String cardNo = request.getParameter("cardNo");
        String cardName = request.getParameter("cardName");
        String employeeNo = request.getParameter("employeeNo");
        String endHour = request.getParameter("endHour");
        Date now = new Date();
        NetDvrTimeEx start = new NetDvrTimeEx();
        start.setwYear((short) now.getYear());
        start.setByMonth((byte) now.getMonth());
        start.setByDay((byte) now.getDay());
        start.setByHour((byte) now.getHours());
        start.setByMinute((byte) now.getMinutes());
        start.setBySecond((byte) 0);
        NetDvrTimeEx end = new NetDvrTimeEx();
        end.setwYear((short) 2018);
        end.setByMonth((byte) 9);
        end.setByDay((byte) 25);
        end.setByHour((byte) Integer.valueOf(endHour).intValue());
        end.setByMinute((byte) 0);
        end.setBySecond((byte) 0);
        ApiResult apiResult = cardService.setCardInfo("", cardNo, cardName, "", Integer.valueOf(employeeNo), start, end);
        return apiResult.toJSon();
    }

    @ResponseBody
    @RequestMapping(value = "getCard")
    public String getCardInfo(HttpServletRequest request) {
        String cardNo = request.getParameter("cardNo");
        return cardService.getCardInfo(cardNo).toJSon();
    }
}
