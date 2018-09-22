package com.ts.hc_ctrl_demo.common.entity;

import com.ts.hc_ctrl_demo.hc_java_sdk.HCNetSDK;

public class AlarmEventDesc {

    private short eventType;

    private String eventTypeDesc;

    private short wPort;

    private byte byChannel;

    private byte byIvmsChannel;

    private String devIp;

    public static AlarmEventDesc buildByStruDevInfo(HCNetSDK.NET_VCA_DEV_INFO struDevInfo) {
        AlarmEventDesc alarmEventDesc = new AlarmEventDesc();
        alarmEventDesc.setwPort(struDevInfo.wPort);
        alarmEventDesc.setByChannel(struDevInfo.byChannel);
        alarmEventDesc.setByIvmsChannel(struDevInfo.byIvmsChannel);
        alarmEventDesc.setDevIp(new String(struDevInfo.struDevIP.sIpV4));
        return alarmEventDesc;
    }

    public short getEventType() {
        return eventType;
    }

    public void setEventType(short eventType) {
        this.eventType = eventType;
    }

    public short getwPort() {
        return wPort;
    }

    public void setwPort(short wPort) {
        this.wPort = wPort;
    }

    public byte getByChannel() {
        return byChannel;
    }

    public void setByChannel(byte byChannel) {
        this.byChannel = byChannel;
    }

    public byte getByIvmsChannel() {
        return byIvmsChannel;
    }

    public void setByIvmsChannel(byte byIvmsChannel) {
        this.byIvmsChannel = byIvmsChannel;
    }

    public String getDevIp() {
        return devIp;
    }

    public void setDevIp(String devIp) {
        this.devIp = devIp;
    }

    public String getEventTypeDesc() {
        return eventTypeDesc;
    }

    public void setEventTypeDesc(String eventTypeDesc) {
        this.eventTypeDesc = eventTypeDesc;
    }
}
