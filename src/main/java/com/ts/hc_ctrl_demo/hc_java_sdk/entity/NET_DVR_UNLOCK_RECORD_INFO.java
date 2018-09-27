package com.ts.hc_ctrl_demo.hc_java_sdk.entity;

import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;

public class NET_DVR_UNLOCK_RECORD_INFO extends Structure {
    public byte byUnlockType;

    public byte[] byRes1 = new byte[3];

    public byte[] byControlSrc = new byte[HCNetSDK.NAME_LEN];

    public int dwPicDataLen;

    public ByteByReference pImage;

    public int dwCardUserID;

    public short nFloorNumber;

    public short wRoomNumber;

    public short wLockID;

    public byte[] byRes = new byte[202];

}
