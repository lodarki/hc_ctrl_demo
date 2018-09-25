package com.ts.hc_ctrl_demo.service;

import com.sun.jna.NativeLong;
import com.ts.hc_ctrl_demo.common.entity.ApiResult;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.SDKInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ListenService {

    //监听标识符
    private static NativeLong lListenHandleFlag = new NativeLong(-1);

    @Resource
    private HCNetSDK.FMSGCallBack listenHandler;

    @Value("${hc.local.host:172.16.0.113}")
    private String localHost;

    @Value("${hc.local.port:7200}")
    private int localPort;

    /**
     * 开启监听
     * @return
     */
    public ApiResult startAlarmListen() {
        lListenHandleFlag = SDKInstance.HC.NET_DVR_StartListen_V30(localHost, (short) localPort, listenHandler, null);
        if (lListenHandleFlag.intValue() < 0) {
            return ApiResult.Error(500, "启动监听失败！");
        } else {
            return ApiResult.Ok("启动监听成功!");
        }
    }

    /**
     * 停止监听
     * @return
     */
    public ApiResult stopAlarmListen() {
        if (lListenHandleFlag.intValue() < 0) {
            return ApiResult.Ok("无监听，无需停止!");
        }
        if (!SDKInstance.HC.NET_DVR_StopListen_V30(lListenHandleFlag)) {
            return ApiResult.Error(500, "停止监听失败！");
        } else {
            return ApiResult.Ok("停止监听成功!");
        }
    }
}
