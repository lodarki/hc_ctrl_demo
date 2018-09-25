package com.ts.hc_ctrl_demo.service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.callBackHandler.CardGetHandler;
import com.ts.hc_ctrl_demo.callBackHandler.CardSendHandler;
import com.ts.hc_ctrl_demo.common.entity.ApiResult;
import com.ts.hc_ctrl_demo.common.utils.AsyncUtil;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.NetDvrTimeEx;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.SDKInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CardService {

    @Resource
    private CardSendHandler cardSendHandler;
    @Resource
    private CardGetHandler cardGetHandler;

    public static ConcurrentHashMap<String, NativeLong> CardSendConnMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, NativeLong> CardGetConnMap = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(CardService.class);

    /**
     * 下发卡片信息
     * @param userId
     * @param cardNo
     * @param cardName
     * @param password
     * @param employeeNo
     * @param startTimeEx
     * @param endTimeEx
     * @return
     */
    public ApiResult setCardInfo(String userId,
                                 String cardNo,
                                 String cardName,
                                 String password,
                                 int employeeNo,
                                 NetDvrTimeEx startTimeEx,
                                 NetDvrTimeEx endTimeEx) {

        NativeLong cardSendFtpFlag = buildSendCardTcpCon(SDKInstance.HC);
        if (cardSendFtpFlag.intValue() < 0) {
            return ApiResult.Error(500, "建立长连接失败，错误号：" + SDKInstance.HC.NET_DVR_GetLastError());
        }

        CardSendConnMap.put(cardNo, cardSendFtpFlag);
        // 设置卡参数
        HCNetSDK.NET_DVR_CARD_CFG_V50 struCardInfo = new HCNetSDK.NET_DVR_CARD_CFG_V50(); //卡参数
        struCardInfo.read();
        struCardInfo.dwSize = struCardInfo.size();
        struCardInfo.dwModifyParamType = 0x00000001 + 0x00000002 + 0x00000004 + 0x00000008 +
                0x00000010 + 0x00000020 + 0x00000080 + 0x00000100 + 0x00000200 + 0x00000400 + 0x00000800;
        /*
         * #define CARD_PARAM_CARD_VALID       0x00000001  //卡是否有效参数
         * #define CARD_PARAM_VALID            0x00000002  //有效期参数
         * #define CARD_PARAM_CARD_TYPE        0x00000004  //卡类型参数
         * #define CARD_PARAM_DOOR_RIGHT       0x00000008  //门权限参数
         * #define CARD_PARAM_LEADER_CARD      0x00000010  //首卡参数
         * #define CARD_PARAM_SWIPE_NUM        0x00000020  //最大刷卡次数参数
         * #define CARD_PARAM_GROUP            0x00000040  //所属群组参数
         * #define CARD_PARAM_PASSWORD         0x00000080  //卡密码参数
         * #define CARD_PARAM_RIGHT_PLAN       0x00000100  //卡权限计划参数
         * #define CARD_PARAM_SWIPED_NUM       0x00000200  //已刷卡次数
         * #define CARD_PARAM_EMPLOYEE_NO      0x00000400  //工号
         * #define CARD_PARAM_NAME             0x00000800  //姓名
         */

        for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
            struCardInfo.byCardNo[i] = 0;
        }
        System.arraycopy(cardNo.getBytes(), 0, struCardInfo.byCardNo, 0, cardNo.length());

        struCardInfo.byCardValid = 1;
        struCardInfo.byCardType = 1;
        struCardInfo.byLeaderCard = 0;
        struCardInfo.byDoorRight[0] = 1; //门1有权限
        struCardInfo.wCardRightPlan[0].wRightPlan[0] = 1; //门1关联卡参数计划模板1

        //卡有效期
        struCardInfo.struValid.byEnable = 1;
        struCardInfo.struValid.struBeginTime.wYear = startTimeEx.getwYear();
        struCardInfo.struValid.struBeginTime.byMonth = startTimeEx.getByMonth();
        struCardInfo.struValid.struBeginTime.byDay = startTimeEx.getByDay();
        struCardInfo.struValid.struBeginTime.byHour = startTimeEx.getByHour();
        struCardInfo.struValid.struBeginTime.byMinute = startTimeEx.getByMinute();
        struCardInfo.struValid.struBeginTime.bySecond = startTimeEx.getBySecond();
        struCardInfo.struValid.struEndTime.wYear = endTimeEx.getwYear();
        struCardInfo.struValid.struEndTime.byMonth = endTimeEx.getByMonth();
        struCardInfo.struValid.struEndTime.byDay = endTimeEx.getByDay();
        struCardInfo.struValid.struEndTime.byHour = endTimeEx.getByHour();
        struCardInfo.struValid.struEndTime.byMinute = endTimeEx.getByMinute();
        struCardInfo.struValid.struEndTime.bySecond = endTimeEx.getBySecond();

        struCardInfo.dwMaxSwipeTime = 0; //无次数限制
        struCardInfo.dwSwipeTime = 0; //已刷卡次数
        struCardInfo.byCardPassword = password.getBytes();
        struCardInfo.dwEmployeeNo = employeeNo; //工号

        // 设置卡片名称
        try {
            byte[] nameBytes = cardName.getBytes("GBK");
            for (int i = 0; i < HCNetSDK.NAME_LEN; i++) {
                struCardInfo.byName[i] = 0;
            }
            System.arraycopy(nameBytes, 0, struCardInfo.byName, 0, nameBytes.length);
        } catch (UnsupportedEncodingException e) {
            logger.error("card name get bytes error :", e);
        }

        struCardInfo.write();
        Pointer pSendBufSet = struCardInfo.getPointer();

        // 发送卡信息
        if (!SDKInstance.HC.NET_DVR_SendRemoteConfig(cardSendFtpFlag, 0x3, pSendBufSet, struCardInfo.size())) {
            return ApiResult.Error(500, "ENUM_ACS_SEND_DATA失败，错误号：" + SDKInstance.HC.NET_DVR_GetLastError());
        }

        AsyncUtil.runAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!SDKInstance.HC.NET_DVR_StopRemoteConfig(cardSendFtpFlag)) {
                logger.error("断开长连接失败，错误号： {} ", SDKInstance.HC.NET_DVR_GetLastError());
            } else {
                logger.info("长连接断开成功！");
            }
        });

        return ApiResult.Ok("卡号信息下发成功！");
    }

    /**
     * 断开卡号获取长连接
     * @param cardGetFtpFlag
     * @return
     */
    public boolean stopCardGetTcpCon(NativeLong cardGetFtpFlag) {
        if (cardGetFtpFlag.intValue() < 0) {
            logger.info("没有卡号获取连接建立，无需断开！");
            return true;
        }
        boolean stopSuc = SDKInstance.HC.NET_DVR_StopRemoteConfig(cardGetFtpFlag);
        if (!stopSuc) {
            logger.error("断开卡号获取长连接失败，错误号：" + SDKInstance.HC.NET_DVR_GetLastError());
            return false;
        }
        return true;
    }

    /**
     * 创建卡号下发的长连接
     * @param hcNetSDK
     * @return
     */
    private NativeLong buildSendCardTcpCon(HCNetSDK hcNetSDK) {
        //创建下发卡号的ftp长连接
        HCNetSDK.NET_DVR_CARD_CFG_COND m_struCardInputParamSet = new HCNetSDK.NET_DVR_CARD_CFG_COND();
        m_struCardInputParamSet.read();
        m_struCardInputParamSet.dwSize = m_struCardInputParamSet.size();
        m_struCardInputParamSet.dwCardNum = 1;
        m_struCardInputParamSet.byCheckCardNo = 1;

        Pointer lpInBuffer = m_struCardInputParamSet.getPointer();
        m_struCardInputParamSet.write();

        Pointer pUserData = null;
        NativeLong conFlag = hcNetSDK.NET_DVR_StartRemoteConfig(LoginService.lUserID, HCNetSDK.NET_DVR_SET_CARD_CFG_V50, lpInBuffer, m_struCardInputParamSet.size(), cardSendHandler, pUserData);
        cardSendHandler.setConnFlag(conFlag);
        return conFlag;
    }

    /**
     * 创建卡号查询的长连接
     * @param hcNetSDK
     * @return
     */
    private NativeLong buildGetCardTcpCon(HCNetSDK hcNetSDK) {
        HCNetSDK.NET_DVR_CARD_CFG_COND m_struCardInputParam = new HCNetSDK.NET_DVR_CARD_CFG_COND();
        m_struCardInputParam.dwSize = m_struCardInputParam.size();
        m_struCardInputParam.dwCardNum = 0xffffffff; //查找全部
        m_struCardInputParam.byCheckCardNo = 1;

        Pointer lpInBuffer = m_struCardInputParam.getPointer();
        m_struCardInputParam.write();
        Pointer pUserData = null;

        return hcNetSDK.NET_DVR_StartRemoteConfig(LoginService.lUserID, HCNetSDK.NET_DVR_GET_CARD_CFG_V50, lpInBuffer, m_struCardInputParam.size(), cardGetHandler, pUserData);
    }

    public ApiResult getCardInfo(String cardNo) {

        NativeLong cardGetFtpFlag = buildGetCardTcpCon(SDKInstance.HC);
        if (cardGetFtpFlag.intValue() < -1) {
            return ApiResult.Error(500, "建立长连接失败，错误号：" + SDKInstance.HC.NET_DVR_GetLastError());
        }

        logger.info("建立获取卡参数长连接成功!");

        CardGetConnMap.put(cardNo, cardGetFtpFlag);

        //查找指定卡号
        HCNetSDK.NET_DVR_CARD_CFG_SEND_DATA m_struCardSendInputParam = new HCNetSDK.NET_DVR_CARD_CFG_SEND_DATA();
        m_struCardSendInputParam.read();
        m_struCardSendInputParam.dwSize = m_struCardSendInputParam.size();
        m_struCardSendInputParam.byCardNo = cardNo.getBytes();
        m_struCardSendInputParam.byRes = "0".getBytes();

        Pointer pSendBuf = m_struCardSendInputParam.getPointer();
        m_struCardSendInputParam.write();

        if (!SDKInstance.HC.NET_DVR_SendRemoteConfig(cardGetFtpFlag, 0x3, pSendBuf, m_struCardSendInputParam.size())) {
            return ApiResult.Error(500, "ENUM_ACS_SEND_DATA失败，错误号：" + SDKInstance.HC.NET_DVR_GetLastError());
        }

        return ApiResult.Ok("卡号查询请求已发送成功!");
    }
}
