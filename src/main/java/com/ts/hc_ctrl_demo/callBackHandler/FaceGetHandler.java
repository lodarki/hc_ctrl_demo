package com.ts.hc_ctrl_demo.callBackHandler;

import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.service.CallBack4FaceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FaceGetHandler implements HCNetSDK.FRemoteConfigCallback {

    @Resource
    private CallBack4FaceService callBack4FaceService;

    @Override
    public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        callBack4FaceService.noticeFaceGet(dwType, lpBuffer, dwBufLen, pUserData);
    }
}
