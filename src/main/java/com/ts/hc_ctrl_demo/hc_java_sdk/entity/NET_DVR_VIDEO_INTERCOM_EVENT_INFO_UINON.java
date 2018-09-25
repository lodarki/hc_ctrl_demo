package com.ts.hc_ctrl_demo.hc_java_sdk.entity;

import com.sun.jna.Structure;

public class NET_DVR_VIDEO_INTERCOM_EVENT_INFO_UINON extends Structure {
    public byte[] byLen = new byte[256];

    public NET_DVR_UNLOCK_RECORD_INFO struUnlockRecord;

    public NET_DVR_NOTICEDATA_RECEIPT_INFO struNoticedataReceipt;

    public NET_DVR_SEND_CARD_INFO struSendCardInfo;

    public NET_DVR_AUTH_INFO struAuthInfo;
}
