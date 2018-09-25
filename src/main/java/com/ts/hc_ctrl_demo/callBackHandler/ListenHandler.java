package com.ts.hc_ctrl_demo.callBackHandler;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.service.CallBack4AlarmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("listenHandler")
public class ListenHandler implements HCNetSDK.FMSGCallBack {

    @Resource
    private CallBack4AlarmService callBack4AlarmService;

    @Override
    public void invoke(NativeLong lCommand,
                       HCNetSDK.NET_DVR_ALARMER pAlarmer,
                       Pointer pAlarmInfo,
                       int dwBufLen,
                       Pointer pUser) {

        System.out.println(String.format("lCommand : %d", lCommand.intValue()));
        callBack4AlarmService.alarmNotice(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
    }
}
