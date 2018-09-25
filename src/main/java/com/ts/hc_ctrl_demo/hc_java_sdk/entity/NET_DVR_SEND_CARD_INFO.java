package com.ts.hc_ctrl_demo.hc_java_sdk.entity;

import com.sun.jna.Structure;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;

public class NET_DVR_SEND_CARD_INFO extends Structure {

    public byte[] byCardNo = new byte[HCNetSDK.ACS_CARD_NO_LEN];

    public byte[] byuRes = new byte[224];
}
