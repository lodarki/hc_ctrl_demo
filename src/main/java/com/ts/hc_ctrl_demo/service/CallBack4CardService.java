package com.ts.hc_ctrl_demo.service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class CallBack4CardService {

    @Resource
    private CardService cardService;

    private Logger logger = LoggerFactory.getLogger(CallBack4CardService.class);

    public void noticeCardSet(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData, NativeLong connFlag) {
        switch (dwType) {
            case 0:// NET_SDK_CALLBACK_TYPE_STATUS
                HCNetSDK.REMOTECONFIGSTATUS_CARD struCardStatus = new HCNetSDK.REMOTECONFIGSTATUS_CARD();
                struCardStatus.write();
                Pointer pInfoV30 = struCardStatus.getPointer();
                pInfoV30.write(0, lpBuffer.getByteArray(0, struCardStatus.size()), 0, struCardStatus.size());
                struCardStatus.read();

                int iStatus = 0;
                for (int i = 0; i < 4; i++) {
                    int ioffset = i * 8;
                    int iByte = struCardStatus.byStatus[i] & 0xff;
                    iStatus = iStatus + (iByte << ioffset);
                }

                String cardNoStr = new String(struCardStatus.byCardNum).trim();
                switch (iStatus) {
                    case 1000:// NET_SDK_CALLBACK_STATUS_SUCCESS
                        logger.info("下发卡参数成功,dwStatus: {}", iStatus);
                        break;
                    case 1001:
                        logger.info("正在下发卡参数中,dwStatus:", iStatus);
                        logger.info("byCardNum : {}", cardNoStr);
                        break;
                    case 1002:
                        int iErrorCode = 0;
                        for (int i = 0; i < 4; i++) {
                            int ioffset = i * 8;
                            int iByte = struCardStatus.byErrorCode[i] & 0xff;
                            iErrorCode = iErrorCode + (iByte << ioffset);
                        }
                        logger.info("下发卡参数失败, dwStatus: {}, 错误号: {}", iStatus, iErrorCode);
                        break;
                }
                break;
            default:
                logger.info("go card send default process ");
                break;
        }
    }

    public void noticeCardGet(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        logger.info("长连接回调获取数据,NET_SDK_CALLBACK_TYPE_STATUS:" + dwType);
        switch (dwType) {
            case 0: //NET_SDK_CALLBACK_TYPE_STATUS
                HCNetSDK.REMOTECONFIGSTATUS_CARD struCfgStatus = new HCNetSDK.REMOTECONFIGSTATUS_CARD();
                struCfgStatus.write();
                Pointer pCfgStatus = struCfgStatus.getPointer();
                pCfgStatus.write(0, lpBuffer.getByteArray(0, struCfgStatus.size()), 0, struCfgStatus.size());
                struCfgStatus.read();

                int iStatus = 0;
                for (int i = 0; i < 4; i++) {
                    int ioffset = i * 8;
                    int iByte = struCfgStatus.byStatus[i] & 0xff;
                    iStatus = iStatus + (iByte << ioffset);
                }

                switch (iStatus) {
                    case 1000:// NET_SDK_CALLBACK_STATUS_SUCCESS
                        logger.info("查询卡参数成功,dwStatus:" + iStatus);
                        break;
                    case 1001:
                        logger.info("正在查询卡参数中,dwStatus:" + iStatus);
                        break;
                    case 1002:
                        int iErrorCode = 0;
                        for (int i = 0; i < 4; i++) {
                            int ioffset = i * 8;
                            int iByte = struCfgStatus.byErrorCode[i] & 0xff;
                            iErrorCode = iErrorCode + (iByte << ioffset);
                        }
                        logger.info("查询卡参数失败, dwStatus:" + iStatus + "错误号:" + iErrorCode);
                        break;
                }
                break;
            case 2: //NET_SDK_CALLBACK_TYPE_DATA
                HCNetSDK.NET_DVR_CARD_CFG_V50 m_struCardInfo = new HCNetSDK.NET_DVR_CARD_CFG_V50();
                m_struCardInfo.write();
                Pointer pInfoV30 = m_struCardInfo.getPointer();
                pInfoV30.write(0, lpBuffer.getByteArray(0, m_struCardInfo.size()), 0, m_struCardInfo.size());
                m_struCardInfo.read();
                String str = new String(m_struCardInfo.byCardNo).trim();

                try {
                    String srtName = new String(m_struCardInfo.byName, "GBK").trim(); //姓名
                    logger.info("查询到的卡号, getCardNo: {} 姓名: {}", str, srtName);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    logger.error("IOException", e);
                }
                break;
            default:
                break;
        }
    }
}
