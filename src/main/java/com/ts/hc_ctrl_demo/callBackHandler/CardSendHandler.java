package com.ts.hc_ctrl_demo.callBackHandler;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.service.CallBack4CardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.annotation.Native;

@Service("cardHandler")
public class CardSendHandler implements HCNetSDK.FRemoteConfigCallback {

    @Resource
    private CallBack4CardService callBack4CardService;

    private NativeLong connFlag = new NativeLong(-1);

    public NativeLong getConnFlag() {
        return connFlag;
    }

    public void setConnFlag(NativeLong connFlag) {
        this.connFlag = connFlag;
    }

    @Override
    public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        callBack4CardService.noticeCardSet(dwType, lpBuffer, dwBufLen, pUserData, this.connFlag);
    }
}
