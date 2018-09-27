package com.ts.hc_ctrl_demo.hc_java_sdk.Utils;

import com.ts.hc_ctrl_demo.hc_java_sdk.entity.NetDvrTimeEx;

import java.util.Date;

public class TimeUtils {

    public static NetDvrTimeEx buildNetDvrTimeEx(Date date) {
        NetDvrTimeEx netDvrTimeEx = new NetDvrTimeEx();
        netDvrTimeEx.setwYear((short) date.getYear());
        netDvrTimeEx.setByMonth((byte) date.getMonth());
        netDvrTimeEx.setByDay((byte) date.getDay());
        netDvrTimeEx.setByHour((byte) date.getHours());
        netDvrTimeEx.setByMinute((byte) date.getMinutes());
        netDvrTimeEx.setBySecond((byte) date.getSeconds());
        return netDvrTimeEx;
    }
}
