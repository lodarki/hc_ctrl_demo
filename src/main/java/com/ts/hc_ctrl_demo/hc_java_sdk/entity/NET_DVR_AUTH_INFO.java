package com.ts.hc_ctrl_demo.hc_java_sdk.entity;

import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;

public class NET_DVR_AUTH_INFO extends Structure {

    public byte byAuthResult;

    public byte byAuthType;

    public byte[] byRes1 = new byte[2];

    public byte[] byCardNo = new byte[HCNetSDK.ACS_CARD_NO_LEN];

    public int dwPicDataLen;

    public ByteByReference pImage;

    public byte[] byRes = new byte[212];
}
