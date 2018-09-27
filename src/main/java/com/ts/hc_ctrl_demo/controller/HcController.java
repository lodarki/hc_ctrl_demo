package com.ts.hc_ctrl_demo.controller;

import com.ts.hc_ctrl_demo.common.entity.ApiResult;
import com.ts.hc_ctrl_demo.hc_java_sdk.Utils.TimeUtils;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.NetDvrTimeEx;
import com.ts.hc_ctrl_demo.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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

    @Resource
    private FaceService faceService;

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
        String lastHour = request.getParameter("lastHour");

        int hour = Integer.valueOf(lastHour);
        if (hour == 0) {
            return ApiResult.Error(201, "持续小时数不正确！").toJSon();
        }

        Date now = new Date();
        NetDvrTimeEx start = TimeUtils.buildNetDvrTimeEx(now);
        NetDvrTimeEx end = TimeUtils.buildNetDvrTimeEx(DateUtils.addHours(now, hour));

        ApiResult apiResult = cardService.setCardInfo(cardNo, cardName, "", Integer.valueOf(employeeNo), start, end);
        return apiResult.toJSon();
    }

    @ResponseBody
    @RequestMapping(value = "/delCard")
    public String delCard(@RequestParam(value = "cardNo") String cardNo) {
        return cardService.delCardInfo(cardNo).toJSon();
    }

    @ResponseBody
    @RequestMapping(value = "/getCard")
    public String getCardInfo(HttpServletRequest request) {
        String cardNo = request.getParameter("cardNo");
        return cardService.getCardInfo(cardNo).toJSon();
    }

    @ResponseBody
    @RequestMapping(value = "/setFace")
    public String setFaceInfo(MultipartHttpServletRequest request) {

        String cardNo = request.getParameter("cardNo");
        MultipartFile picFile = request.getFile("picFile");

        if (StringUtils.isEmpty(cardNo)) {
            return ApiResult.Error(201, "卡号非法！").toJSon();
        }

        if (picFile == null) {
            return ApiResult.Error(201, "缺少文件数据！").toJSon();
        }

        try {
            return faceService.setFaceInfo(cardNo, picFile.getBytes()).toJSon();
        } catch (IOException e) {
            return ApiResult.Error(500, e.getMessage()).toJSon();
        }
    }
}
