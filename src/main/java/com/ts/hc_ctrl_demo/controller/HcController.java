package com.ts.hc_ctrl_demo.controller;

import com.ts.hc_ctrl_demo.common.entity.ApiResult;
import com.ts.hc_ctrl_demo.service.AlarmService;
import com.ts.hc_ctrl_demo.service.ListenService;
import com.ts.hc_ctrl_demo.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

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
    @RequestMapping(value = "/logout")
    public String logout() {
        if (loginService.logout()) {
            return ApiResult.Ok("注销成功！").toJSon();
        }
        return ApiResult.Error(500, "注销失败！").toJSon();
    }
}
