package com.ts.hc_ctrl_demo.callBackHandler;

import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import org.springframework.stereotype.Service;

@Service
public class CardGetHandler implements HCNetSDK.FRemoteConfigCallback{
    @Override
    public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {

    }
}
