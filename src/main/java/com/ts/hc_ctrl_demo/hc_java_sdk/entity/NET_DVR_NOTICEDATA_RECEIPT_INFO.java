package com.ts.hc_ctrl_demo.hc_java_sdk.entity;

import com.sun.jna.Structure;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;

public class NET_DVR_NOTICEDATA_RECEIPT_INFO extends Structure {

    public byte[] byNoticeNumber = new byte[HCNetSDK.MAX_NOTICE_NUMBER_LEN];
    public byte[] byRes = new byte[224];
}
