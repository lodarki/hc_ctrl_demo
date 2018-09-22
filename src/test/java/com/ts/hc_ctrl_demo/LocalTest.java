package com.ts.hc_ctrl_demo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalTest {
    public static void main(String[] args) {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println(hostAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
