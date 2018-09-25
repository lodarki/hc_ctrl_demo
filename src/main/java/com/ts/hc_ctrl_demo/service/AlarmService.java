package com.ts.hc_ctrl_demo.service;

import com.sun.jna.NativeLong;
import com.ts.hc_ctrl_demo.common.entity.ApiResult;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.SDKInstance;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AlarmService {

    //布防标识符
    private static NativeLong lAlarmHandleFlag = new NativeLong(-1);

    @Resource
    private HCNetSDK.FMSGCallBack_V31 alarmHandler;

    /**
     * 布防
     *
     * @param lUserID 海康注册成功后返回的userId
     * @return
     */
    public ApiResult setupAlarmChan(NativeLong lUserID) {
        if (lUserID.intValue() == -1) {
            return ApiResult.Error(205, "请先注册！");
        }
        if (lAlarmHandleFlag.intValue() >= 0) {
            return ApiResult.Error(205, "已经布防过了！");
        }

        if (!SDKInstance.HC.NET_DVR_SetDVRMessageCallBack_V31(alarmHandler, null)) {
            return ApiResult.Error(500, "设置回调函数失败！");
        }
        HCNetSDK.NET_DVR_SETUPALARM_PARAM strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
        strAlarmInfo.dwSize = strAlarmInfo.size();
        strAlarmInfo.byLevel = 1;
        strAlarmInfo.byAlarmInfoType = 1;
        strAlarmInfo.write();
        lAlarmHandleFlag = SDKInstance.HC.NET_DVR_SetupAlarmChan_V41(lUserID, strAlarmInfo);
        if (lAlarmHandleFlag.intValue() == -1) {
            return ApiResult.Error(500, "布防失败！");
        } else {
            return ApiResult.Ok("布防成功!");
        }
    }

    public ApiResult closeAlarmChan() {
        if (lAlarmHandleFlag.intValue() > -1) {
            if (!SDKInstance.HC.NET_DVR_CloseAlarmChan_V30(lAlarmHandleFlag)) {
                return ApiResult.Error(500, "撤防失败!");
            } else {
                lAlarmHandleFlag = new NativeLong(-1);
            }
        }
        return ApiResult.Ok("撤防成功！");
    }
}
