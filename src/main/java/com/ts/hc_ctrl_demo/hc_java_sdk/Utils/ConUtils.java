package com.ts.hc_ctrl_demo.hc_java_sdk.Utils;

import com.sun.jna.NativeLong;
import com.ts.hc_ctrl_demo.common.utils.AsyncUtil;
import com.ts.hc_ctrl_demo.hc_java_sdk.entity.SDKInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConUtils {

    private static Logger logger = LoggerFactory.getLogger(ConUtils.class);

    public static void syncStopRemoteConfig(NativeLong conFlag) {
        AsyncUtil.runAsync(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!SDKInstance.HC.NET_DVR_StopRemoteConfig(conFlag)) {
                logger.error("断开长连接失败，错误号： {} ", SDKInstance.HC.NET_DVR_GetLastError());
            } else {
                logger.info("长连接断开成功！");
            }
        });
    }
}
