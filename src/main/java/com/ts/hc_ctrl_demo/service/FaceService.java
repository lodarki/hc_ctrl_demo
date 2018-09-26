package com.ts.hc_ctrl_demo.service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.callBackHandler.FaceSendHandler;
import com.ts.hc_ctrl_demo.common.entity.ApiResult;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.hc_java_sdk.Utils.ConUtils;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.SDKInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class FaceService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private FaceSendHandler faceSendHandler;

    public ApiResult setFaceInfo(String cardNo, String picName) {

        NativeLong conFlag = buildFaceSetCon(cardNo);
        if (conFlag.intValue() < 0) {
            return ApiResult.Error(500, "人脸下发长连接创建失败！");
        }

        logger.info("人脸数据下发长连接建立成功！");

        HCNetSDK.NET_DVR_FACE_PARAM_CFG config = new HCNetSDK.NET_DVR_FACE_PARAM_CFG();
        config.read();
        config.dwSize = config.size();
        config.byCardNo = cardNo.getBytes();

        try {
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\44218\\OneDrive\\Pictures\\" + picName + ".jpg"));
            int picDataLength = fis.available();
            HCNetSDK.BYTE_ARRAY ptrPicByte = new HCNetSDK.BYTE_ARRAY(picDataLength);
            int read = fis.read(ptrPicByte.byValue);
            if (read < 0) {
                return ApiResult.Error(500, "文件读取错误！");
            }
            ptrPicByte.write();
            config.dwFaceLen = picDataLength;
            config.pFaceBuffer = ptrPicByte.getPointer();
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException", e);
            return ApiResult.Error(500, "照片获取异常！");
        } catch (IOException e) {
            logger.error("picDataLength", e);
            return ApiResult.Error(500, "照片长度读取异常！");
        }

        config.byEnableCardReader[0] = 1;
        config.byFaceID = 1;
        config.byFaceDataType = 1;
        config.write();
        ConUtils.syncStopRemoteConfig(conFlag);

        if (!SDKInstance.HC.NET_DVR_SendRemoteConfig(conFlag, 0x9, config.getPointer(), config.size())) {
            return ApiResult.Error(500, "NET_DVR_SendRemoteConfig失败，错误号：" + SDKInstance.HC.NET_DVR_GetLastError());
        }

        return ApiResult.Ok("成功执行人脸数据的下发！");
    }

    private NativeLong buildFaceSetCon(String cardNo) {
        HCNetSDK.NET_DVR_FACE_PARAM_COND cond = new HCNetSDK.NET_DVR_FACE_PARAM_COND();
        cond.dwSize = cond.size();
        cond.byCardNo = cardNo.getBytes();
        cond.byEnableCardReader[0] = 1;
        cond.dwFaceNum = 1;
        cond.byFaceID = 1;
        cond.write();

        Pointer lpInBuffer = cond.getPointer();
        return SDKInstance.HC.NET_DVR_StartRemoteConfig(LoginService.lUserID, HCNetSDK.NET_DVR_SET_FACE_PARAM_CFG, lpInBuffer, cond.size(), faceSendHandler, null);
    }
}
