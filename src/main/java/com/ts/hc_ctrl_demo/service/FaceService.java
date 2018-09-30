package com.ts.hc_ctrl_demo.service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.callBackHandler.FaceGetHandler;
import com.ts.hc_ctrl_demo.callBackHandler.FaceSendHandler;
import com.ts.hc_ctrl_demo.common.entity.ApiResult;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.hc_java_sdk.Utils.ConUtils;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.SDKInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.*;

@Service
public class FaceService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private FaceSendHandler faceSendHandler;

    @Resource
    private FaceGetHandler faceGetHandler;

    public ApiResult setFaceInfo(String cardNo, byte[] picFile) {

        NativeLong conFlag = buildFaceSetCon(cardNo);
        if (conFlag.intValue() < 0) {
            return ApiResult.Error(500, "人脸下发长连接创建失败！");
        }

        logger.info("人脸数据下发长连接建立成功！");

        HCNetSDK.NET_DVR_FACE_PARAM_CFG config = new HCNetSDK.NET_DVR_FACE_PARAM_CFG();
        config.read();
        config.dwSize = config.size();
        config.byCardNo = cardNo.getBytes();

        int picDataLength = picFile.length;
        HCNetSDK.BYTE_ARRAY ptrPicByte = new HCNetSDK.BYTE_ARRAY(picDataLength);
        ptrPicByte.byValue = picFile;
        ptrPicByte.write();
        config.dwFaceLen = picDataLength;
        config.pFaceBuffer = ptrPicByte.getPointer();

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

    public String getFaceCfg() {
        int iErr = 0;
        HCNetSDK.NET_DVR_FACE_PARAM_COND m_struFaceInputParam = new HCNetSDK.NET_DVR_FACE_PARAM_COND();
        m_struFaceInputParam.dwSize = m_struFaceInputParam.size();
        m_struFaceInputParam.byCardNo = "070759".getBytes(); //人脸关联的卡号
        m_struFaceInputParam.byEnableCardReader[0] = 1;
        m_struFaceInputParam.dwFaceNum = 1;
        m_struFaceInputParam.byFaceID = 1;
        m_struFaceInputParam.write();

        Pointer lpInBuffer = m_struFaceInputParam.getPointer();
        Pointer pUserData = null;

        NativeLong lHandle = SDKInstance.HC.NET_DVR_StartRemoteConfig(LoginService.lUserID, HCNetSDK.NET_DVR_GET_FACE_PARAM_CFG, lpInBuffer, m_struFaceInputParam.size(), faceGetHandler, pUserData);
        if (lHandle.intValue() < 0) {
            iErr = SDKInstance.HC.NET_DVR_GetLastError();
            logger.info("建立长连接失败，错误号：" + iErr);
            return String.valueOf(iErr);
        }
        logger.info( "建立获取卡参数长连接成功!");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!SDKInstance.HC.NET_DVR_StopRemoteConfig(lHandle)) {
            iErr = SDKInstance.HC.NET_DVR_GetLastError();
            logger.info("断开长连接失败，错误号：" + iErr);
            return String.valueOf(iErr);
        }
        logger.info("断开长连接成功!");
        return "";
    }

    public String delFace(String cardNo) {

        int iErr = 0;
        //删除人脸数据
        HCNetSDK.NET_DVR_FACE_PARAM_CTRL m_struFaceDel = new HCNetSDK.NET_DVR_FACE_PARAM_CTRL();
        m_struFaceDel.dwSize = m_struFaceDel.size();
        m_struFaceDel.byMode = 0; //删除方式：0- 按卡号方式删除，1- 按读卡器删除

        m_struFaceDel.struProcessMode.setType(HCNetSDK.NET_DVR_FACE_PARAM_BYCARD.class);
        m_struFaceDel.struProcessMode.struByCard.byCardNo = cardNo.getBytes();//需要删除人脸关联的卡号
        m_struFaceDel.struProcessMode.struByCard.byEnableCardReader[0] = 1; //读卡器
        m_struFaceDel.struProcessMode.struByCard.byFaceID[0] = 1; //人脸ID
        m_struFaceDel.write();

        Pointer lpInBuffer = m_struFaceDel.getPointer();

        boolean lRemoteCtrl = SDKInstance.HC.NET_DVR_RemoteControl(LoginService.lUserID, HCNetSDK.NET_DVR_DEL_FACE_PARAM_CFG, lpInBuffer, m_struFaceDel.size());
        if (!lRemoteCtrl) {
            iErr = SDKInstance.HC.NET_DVR_GetLastError();
            logger.info("NET_DVR_DEL_FACE_PARAM_CFG删除人脸图片失败，错误号：" + iErr);
        } else {
            logger.info("NET_DVR_DEL_FACE_PARAM_CFG成功!");
        }
        return "adfadsf";
    }
}
