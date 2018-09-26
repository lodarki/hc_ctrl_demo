package com.ts.hc_ctrl_demo.callBackHandler;

import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.service.CallBack4CardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CardGetHandler implements HCNetSDK.FRemoteConfigCallback{

    @Resource
    private CallBack4CardService callBack4CardService;

    @Override
    public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        callBack4CardService.noticeCardGet(dwType, lpBuffer, dwBufLen, pUserData);
    }
}
