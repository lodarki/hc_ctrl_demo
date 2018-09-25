package com.ts.hc_ctrl_demo.hc_java_sdk.entity;

import com.sun.jna.Structure;

public class NET_DVR_NOTICEDATA_RECEIPT_INFO extends Structure {

    public byte[] byNoticeNumber = new byte[128];
    public byte[] byRes = new byte[224];
}
