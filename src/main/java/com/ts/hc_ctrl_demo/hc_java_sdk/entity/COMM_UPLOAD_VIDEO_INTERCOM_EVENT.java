package com.ts.hc_ctrl_demo.hc_java_sdk.entity;

import com.sun.jna.Structure;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;

public class COMM_UPLOAD_VIDEO_INTERCOM_EVENT extends Structure {

    public int dwSize;

    public HCNetSDK.NET_DVR_TIME_EX struTime;

    public byte[] byDevNumber = new byte[HCNetSDK.MAX_DEV_NUMBER_LEN];

    public byte byEventType;

    public byte[] byRes1 = new byte[3];

    public NET_DVR_VIDEO_INTERCOM_EVENT_INFO_UINON uEventInfo;

    public byte[] byRes2 = new byte[256];
}
