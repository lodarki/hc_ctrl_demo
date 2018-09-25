package com.ts.hc_ctrl_demo.service;

import com.sun.jna.NativeLong;
import com.ts.hc_ctrl_demo.common.entity.ApiResult;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.SDKInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoginService {

    private Log logger = LogFactory.getLog(LoginService.class.getName());

    //登陆的用户id
    public static NativeLong lUserID = new NativeLong(-1);

    @Value("${hc.device.ip:172.16.0.65}")
    private String hcDeviceIp;

    @Value("${hc.device.port:8000}")
    private int hcPort;

    @Value("${hc.device.login:admin}")
    private String login;

    @Value("${hc.device.password:ai20082018}")
    private String password;

    @Resource
    private AlarmService alarmService;

    @Resource
    private CardService cardService;

    public boolean login() {
        if (!SDKInstance.HC.NET_DVR_Init()) {
            return false;
        }
        //注册
        hcDeviceIp = "172.16.0.65";
        //注册之前先注销已注册的用户,预览情况下不可注销
        if (lUserID.longValue() > -1) {
            //先注销
            SDKInstance.HC.NET_DVR_Logout(lUserID);
            lUserID = new NativeLong(-1);
        }

        HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        lUserID = SDKInstance.HC.NET_DVR_Login_V30(hcDeviceIp, (short) hcPort, login, password, m_strDeviceInfo);

        long userID = lUserID.longValue();
        return userID != -1;
    }

    public boolean logout() {
        //报警撤防
        ApiResult apiResult = alarmService.closeAlarmChan();
        logger.info(apiResult);
        // 注销和清空资源
        return SDKInstance.HC.NET_DVR_Logout(lUserID) && SDKInstance.HC.NET_DVR_Cleanup();
    }
}
