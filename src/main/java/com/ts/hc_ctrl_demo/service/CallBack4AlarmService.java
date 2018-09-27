package com.ts.hc_ctrl_demo.service;

import com.alibaba.fastjson.JSON;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.ts.hc_ctrl_demo.common.entity.AlarmDesc;
import com.ts.hc_ctrl_demo.common.entity.AlarmEventDesc;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.COMM_UPLOAD_VIDEO_INTERCOM_EVENT;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Service
public class CallBack4AlarmService {

    private Logger logger = LoggerFactory.getLogger(CallBack4AlarmService.class);

    @Value("${ai.local.host:172.16.0.10}")
    private String aiLocalHost;
    @Value("${ai.local.port:9090}")
    private String aiLocalPort;

    public boolean alarmNotice(NativeLong lCommand,
                               HCNetSDK.NET_DVR_ALARMER pAlarmer,
                               Pointer pAlarmInfo,
                               int dwBufLen,
                               Pointer pUser) {

        int alarmType = lCommand.intValue();

        Object entity = null;
        String[] sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
        switch (alarmType) {
            case HCNetSDK.COMM_ALARM_V30:
                logger.info("HCNetSDK.COMM_ALARM_V30");
                entity = COMM_ALARM_V30_info(pAlarmInfo);
                break;
            case HCNetSDK.COMM_ALARM_RULE:
                logger.info("HCNetSDK.COMM_ALARM_RULE");
                entity = COMM_ALARM_RULE_info(pAlarmInfo);
                break;
            case HCNetSDK.COMM_UPLOAD_PLATE_RESULT:
                logger.info("HCNetSDK.COMM_UPLOAD_PLATE_RESULT");
                entity = COMM_UPLOAD_PLATE_RESULT_info(pAlarmInfo);
                break;
            case HCNetSDK.COMM_ITS_PLATE_RESULT:
                logger.info("HCNetSDK.COMM_ITS_PLATE_RESULT");
                entity = COMM_ITS_PLATE_RESULT_info(pAlarmInfo);
                break;
            case HCNetSDK.COMM_ALARM_PDC:
                logger.info("HCNetSDK.COMM_ALARM_PDC");
                entity = COMM_ALARM_PDC_info(pAlarmInfo);
//                sIP = new String(strPDCResult.struDevInfo.struDevIP.sIpV4).split("\0", 2);
                break;
            case HCNetSDK.COMM_ITS_PARK_VEHICLE:
                logger.info("HCNetSDK.COMM_ITS_PARK_VEHICLE");
                entity = COMM_ITS_PARK_VEHICLE_info(pAlarmInfo);
                break;
            case HCNetSDK.COMM_ALARM_ACS: //门禁主机报警信息
                logger.info("HCNetSDK.COMM_ALARM_ACS");
                entity = COMM_ALARM_ACS_info(pAlarmInfo);
                break;
            case HCNetSDK.COMM_ID_INFO_ALARM: //身份证信息
                logger.info("HCNetSDK.COMM_ID_INFO_ALARM");
                entity = COMM_ID_INFO_ALARM_info(pAlarmInfo);
                break;
            case 0x1132: //COMM_UPLOAD_VIDEO_INTERCOM_EVENT 可视对讲事件记录信息
                logger.info("COMM_UPLOAD_VIDEO_INTERCOM_EVENT");
                entity = COMM_UPLOAD_VIDEO_INTERCOM_EVENT_info(pAlarmInfo);
                break;
            default:
                logger.info("go default");
                break;
        }

        //TODO 发送给ai_yuyue
        logger.info(JSON.toJSONString(pAlarmer));
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("entity", entity);
        dataMap.put("ip", sIP);
        logger.info("dataMap : " + JSON.toJSONString(dataMap));
        return sendAlarmToAiServer(dataMap);
    }

    private String COMM_UPLOAD_VIDEO_INTERCOM_EVENT_info(Pointer pAlarmInfo) {

        COMM_UPLOAD_VIDEO_INTERCOM_EVENT strCommUploadVideoIntercomEvent = new COMM_UPLOAD_VIDEO_INTERCOM_EVENT();
        strCommUploadVideoIntercomEvent.write();
        Pointer pCommUploadVideoIntercomEvent = strCommUploadVideoIntercomEvent.getPointer();
        pCommUploadVideoIntercomEvent.write(0, pAlarmInfo.getByteArray(0, strCommUploadVideoIntercomEvent.size()), 0, strCommUploadVideoIntercomEvent.size());
        strCommUploadVideoIntercomEvent.read();

        int cardNoFromSendCardInfo = 0;
        for (int i = 0; i < strCommUploadVideoIntercomEvent.uEventInfo.struSendCardInfo.byCardNo.length; i++) {
            int ioffset = i * 8;
            int iByte = strCommUploadVideoIntercomEvent.uEventInfo.struSendCardInfo.byCardNo[i] & 0xff;
            cardNoFromSendCardInfo = cardNoFromSendCardInfo + (iByte << ioffset);
        }

        int cardNoFromAuthInfo = 0;
        for (int i = 0; i < strCommUploadVideoIntercomEvent.uEventInfo.struAuthInfo.byCardNo.length; i++) {
            int ioffset = i * 8;
            int iByte = strCommUploadVideoIntercomEvent.uEventInfo.struAuthInfo.byCardNo[i] & 0xff;
            cardNoFromAuthInfo = cardNoFromAuthInfo + (iByte << ioffset);
        }

        HashMap<String, Integer> result = new HashMap<>();
        result.put("cardNoFromSendCardInfo", cardNoFromSendCardInfo);
        result.put("cardNoFromAuthInfo", cardNoFromAuthInfo);

        return JSON.toJSONString(result);
    }

    private String COMM_ITS_PARK_VEHICLE_info(Pointer pAlarmInfo) {
        HCNetSDK.NET_ITS_PARK_VEHICLE strItsParkVehicle = new HCNetSDK.NET_ITS_PARK_VEHICLE();
        strItsParkVehicle.write();
        Pointer pItsParkVehicle = strItsParkVehicle.getPointer();
        pItsParkVehicle.write(0, pAlarmInfo.getByteArray(0, strItsParkVehicle.size()), 0, strItsParkVehicle.size());
        strItsParkVehicle.read();
        String sAlarmTypeDesc = "";
        try {
            String srtParkingNo = new String(strItsParkVehicle.byParkingNo).trim(); //车位编号
            String srtPlate = new String(strItsParkVehicle.struPlateInfo.sLicense, "GBK").trim(); //车牌号码
            sAlarmTypeDesc = ",停产场数据,车位编号：" + srtParkingNo + ",车位状态："
                    + strItsParkVehicle.byLocationStatus + ",车牌：" + srtPlate;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            logger.error("COMM_ITS_PARK_VEHICLE_info", e);
        }

        for (int i = 0; i < strItsParkVehicle.dwPicNum; i++) {
            if (strItsParkVehicle.struPicInfo[i].dwDataLen > 0) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                String newName = sf.format(new Date());
                FileOutputStream fos;
                try {
                    String filename = newName + "_ITSPark_type" + strItsParkVehicle.struPicInfo[i].byType + ".jpg";
                    fos = new FileOutputStream(filename);
                    //将字节写入文件
                    long offset = 0;
                    ByteBuffer buffers = strItsParkVehicle.struPicInfo[i].pBuffer.getByteBuffer(offset, strItsParkVehicle.struPicInfo[i].dwDataLen);
                    byte[] bytes = new byte[strItsParkVehicle.struPicInfo[i].dwDataLen];
                    buffers.rewind();
                    buffers.get(bytes);
                    fos.write(bytes);
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    logger.error("COMM_ITS_PARK_VEHICLE_info", e);
                }
            }
        }

        return sAlarmTypeDesc;
    }

    /**
     * 将报警信息发送给AiServer端
     *
     * @param dataMap
     * @return
     */
    private boolean sendAlarmToAiServer(HashMap<String, Object> dataMap) {

        GetMethod getMethod = new GetMethod("http://" + aiLocalHost + ":" + aiLocalPort + "/v1/user/open_ground");
        HttpClient httpClient = new HttpClient();
        HttpClientParams httpClientParams = new HttpClientParams();
        httpClientParams.setParameter("json", JSON.toJSONString(dataMap));
        httpClient.setParams(httpClientParams);
        getMethod.setRequestHeader("Content-type", "application/json; charset=UTF-8");
        getMethod.setRequestHeader("Accept", "application/json; charset=UTF-8");
        try {
            httpClient.executeMethod(getMethod);
            String spJson = getMethod.getResponseBodyAsString();
            logger.info(spJson);
            return true;
        } catch (IOException e) {
            logger.error("sendAlarmToAiServer", e);
        }
        return false;
    }

    private String COMM_ID_INFO_ALARM_info(Pointer pAlarmInfo) {
        HCNetSDK.NET_DVR_ID_CARD_INFO_ALARM strIDCardInfo = new HCNetSDK.NET_DVR_ID_CARD_INFO_ALARM();
        strIDCardInfo.write();
        Pointer pIDCardInfo = strIDCardInfo.getPointer();
        pIDCardInfo.write(0, pAlarmInfo.getByteArray(0, strIDCardInfo.size()), 0, strIDCardInfo.size());
        strIDCardInfo.read();
        return "：门禁身份证刷卡信息，身份证号码：" + new String(strIDCardInfo.struIDCardCfg.byIDNum).trim() + "，姓名：" +
                new String(strIDCardInfo.struIDCardCfg.byName).trim() + "，报警主类型：" + strIDCardInfo.dwMajor + "，报警次类型：" + strIDCardInfo.dwMinor;
    }

    private String COMM_ALARM_ACS_info(Pointer pAlarmInfo) {
        HCNetSDK.NET_DVR_ACS_ALARM_INFO strACSInfo = new HCNetSDK.NET_DVR_ACS_ALARM_INFO();
        strACSInfo.write();
        Pointer pACSInfo = strACSInfo.getPointer();
        pACSInfo.write(0, pAlarmInfo.getByteArray(0, strACSInfo.size()), 0, strACSInfo.size());
        strACSInfo.read();
        String sAlarmTypeDesc = "：门禁主机报警信息，卡号：" + new String(strACSInfo.struAcsEventInfo.byCardNo).trim() + "，卡类型：" +
                strACSInfo.struAcsEventInfo.byCardType + "，报警主类型：" + strACSInfo.dwMajor + "，报警次类型：" + strACSInfo.dwMinor;
        if (strACSInfo.dwPicDataLen > 0) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            String newName = sf.format(new Date());
            FileOutputStream fos;
            try {
                String filename = newName + "_ACS_card_" + new String(strACSInfo.struAcsEventInfo.byCardNo).trim() + ".jpg";
                fos = new FileOutputStream(filename);
                //将字节写入文件
                long offset = 0;
                ByteBuffer buffers = strACSInfo.pPicData.getByteBuffer(offset, strACSInfo.dwPicDataLen);
                byte[] bytes = new byte[strACSInfo.dwPicDataLen];
                buffers.rewind();
                buffers.get(bytes);
                fos.write(bytes);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.error("COMM_ALARM_ACS_info", e);
            }
        }
        return sAlarmTypeDesc;
    }

    private String COMM_ALARM_PDC_info(Pointer pAlarmInfo) {
        HCNetSDK.NET_DVR_PDC_ALRAM_INFO strPDCResult = new HCNetSDK.NET_DVR_PDC_ALRAM_INFO();
        strPDCResult.write();
        Pointer pPDCInfo = strPDCResult.getPointer();
        pPDCInfo.write(0, pAlarmInfo.getByteArray(0, strPDCResult.size()), 0, strPDCResult.size());
        strPDCResult.read();
        return "：客流量统计，进入人数：" + strPDCResult.dwEnterNum + "，离开人数：" + strPDCResult.dwLeaveNum;
    }

    private String COMM_ITS_PLATE_RESULT_info(Pointer pAlarmInfo) {
        HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult = new HCNetSDK.NET_ITS_PLATE_RESULT();
        strItsPlateResult.write();
        Pointer pItsPlateInfo = strItsPlateResult.getPointer();
        pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
        strItsPlateResult.read();

        String sAlarmTypeDesc = "";
        try {
            String srt3 = new String(strItsPlateResult.struPlateInfo.sLicense, "GBK");
            sAlarmTypeDesc = "车辆类型：" + strItsPlateResult.byVehicleType + ",交通抓拍上传，车牌：" + srt3;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            logger.error("UnsupportedEncodingException", e);
        }

        for (int i = 0; i < strItsPlateResult.dwPicNum; i++) {
            if (strItsPlateResult.struPicInfo[i].dwDataLen > 0) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                String newName = sf.format(new Date());
                FileOutputStream fos;
                try {
                    String filename = newName + "_ITSPlateResult_type" + strItsPlateResult.struPicInfo[i].byType + ".jpg";
                    fos = new FileOutputStream(filename);
                    //将字节写入文件
                    long offset = 0;
                    ByteBuffer buffers = strItsPlateResult.struPicInfo[i].pBuffer.getByteBuffer(offset, strItsPlateResult.struPicInfo[i].dwDataLen);
                    byte[] bytes = new byte[strItsPlateResult.struPicInfo[i].dwDataLen];
                    buffers.rewind();
                    buffers.get(bytes);
                    fos.write(bytes);
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    logger.error("COMM_ITS_PLATE_RESULT_info", e);
                }
            }
        }

        return sAlarmTypeDesc;
    }

    private String COMM_UPLOAD_PLATE_RESULT_info(Pointer pAlarmInfo) {
        HCNetSDK.NET_DVR_PLATE_RESULT strPlateResult = new HCNetSDK.NET_DVR_PLATE_RESULT();
        strPlateResult.write();
        Pointer pPlateInfo = strPlateResult.getPointer();
        pPlateInfo.write(0, pAlarmInfo.getByteArray(0, strPlateResult.size()), 0, strPlateResult.size());
        strPlateResult.read();

        String sAlarmTypeDesc = "";
        try {
            String srt3 = new String(strPlateResult.struPlateInfo.sLicense, "GBK");
            sAlarmTypeDesc = "：交通抓拍上传，车牌：" + srt3;
        } catch (UnsupportedEncodingException e) {
            logger.error("COMM_UPLOAD_PLATE_RESULT_info", e);
        }

        if (strPlateResult.dwPicLen > 0) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            String newName = sf.format(new Date());
            FileOutputStream fout;
            try {
                fout = new FileOutputStream(newName + "_PlateResult.jpg");
                //将字节写入文件
                long offset = 0;
                ByteBuffer buffers = strPlateResult.pBuffer1.getByteBuffer(offset, strPlateResult.dwPicLen);
                byte[] bytes = new byte[strPlateResult.dwPicLen];
                buffers.rewind();
                buffers.get(bytes);
                fout.write(bytes);
                fout.close();
            } catch (IOException e) {
                logger.error("COMM_UPLOAD_PLATE_RESULT_info", e);
            }
        }

        return sAlarmTypeDesc;
    }

    private AlarmDesc COMM_ALARM_V30_info(Pointer pAlarmInfo) {
        HCNetSDK.NET_DVR_ALARMINFO_V30 strAlarmInfoV30 = new HCNetSDK.NET_DVR_ALARMINFO_V30();
        strAlarmInfoV30.write();
        Pointer pInfoV30 = strAlarmInfoV30.getPointer();
        pInfoV30.write(0, pAlarmInfo.getByteArray(0, strAlarmInfoV30.size()), 0, strAlarmInfoV30.size());
        strAlarmInfoV30.read();
        int dwAlarmType = strAlarmInfoV30.dwAlarmType;
        AlarmDesc alarmDesc = new AlarmDesc();
        alarmDesc.setTypeCode(dwAlarmType);
        switch (dwAlarmType) {
            case 0:
                alarmDesc.setCodeDesc("信号报警");
                alarmDesc.setMessage(String.format("报警入口： %d", strAlarmInfoV30.dwAlarmInputNumber + 1));
                break;
            case 1:
                alarmDesc.setCodeDesc("硬盘满");
                break;
            case 2:
                alarmDesc.setCodeDesc("信号丢失");
                break;
            case 3:
                alarmDesc.setCodeDesc("移动侦测");
                StringBuilder chNo = new StringBuilder();
                for (int i = 0; i < 64; i++) {
                    if (strAlarmInfoV30.byChannel[i] == 1) {
                        chNo.append("ch").append(i + 1).append(" ");
                    }
                }
                alarmDesc.setMessage("报警通道：" + chNo.toString());
                break;
            case 4:
                alarmDesc.setCodeDesc("硬盘未格式化");
                break;
            case 5:
                alarmDesc.setCodeDesc("读写硬盘出错");
                break;
            case 6:
                alarmDesc.setCodeDesc("遮挡报警");
                break;
            case 7:
                alarmDesc.setCodeDesc("制式不匹配");
                break;
            case 8:
                alarmDesc.setCodeDesc("非法访问");
                break;
        }
        return alarmDesc;
    }

    private AlarmEventDesc COMM_ALARM_RULE_info(Pointer pAlarmInfo) {
        HCNetSDK.NET_VCA_RULE_ALARM strVcaAlarm = new HCNetSDK.NET_VCA_RULE_ALARM();
        strVcaAlarm.write();
        Pointer pVcaInfo = strVcaAlarm.getPointer();
        pVcaInfo.write(0, pAlarmInfo.getByteArray(0, strVcaAlarm.size()), 0, strVcaAlarm.size());
        strVcaAlarm.read();

        short wEventTypeEx = strVcaAlarm.struRuleInfo.wEventTypeEx;
        AlarmEventDesc alarmEventDesc = AlarmEventDesc.buildByStruDevInfo(strVcaAlarm.struDevInfo);
        alarmEventDesc.setEventType(wEventTypeEx);
        String wEventTypeExDesc;
        switch (wEventTypeEx) {
            case 1:
                wEventTypeExDesc = "穿越警戒线";
                break;
            case 2:
                wEventTypeExDesc = "目标进入区域";
                break;
            case 3:
                wEventTypeExDesc = "目标离开区域";
                break;
            default:
                wEventTypeExDesc = "其他行为分析报警";
                break;
        }
        alarmEventDesc.setEventTypeDesc(wEventTypeExDesc);

        // 存储图片
        if (strVcaAlarm.dwPicDataLen > 0) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            String newName = sf.format(new Date());
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(newName + "_VCA.jpg");
                //将字节写入文件
                long offset = 0;
                ByteBuffer buffers = strVcaAlarm.pImage.getPointer().getByteBuffer(offset, strVcaAlarm.dwPicDataLen);
                byte[] bytes = new byte[strVcaAlarm.dwPicDataLen];
                buffers.rewind();
                buffers.get(bytes);
                fos.write(bytes);
                fos.close();
            } catch (IOException e) {
                logger.error("COMM_ALARM_RULE_info", e);
            }
        }

        return alarmEventDesc;
    }
}
