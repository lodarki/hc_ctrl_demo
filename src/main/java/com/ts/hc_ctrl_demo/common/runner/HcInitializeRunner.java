package com.ts.hc_ctrl_demo.common.runner;

import com.ts.hc_ctrl_demo.service.AlarmService;
import com.ts.hc_ctrl_demo.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Order(1)
@Component
public class HcInitializeRunner implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(HcInitializeRunner.class);

    @Resource
    private LoginService loginService;
    @Resource
    private AlarmService alarmService;

    @Override
    public void run(ApplicationArguments args) {
        logger.info("HcInitializeRunner run");
        if (loginService.login()) {
            logger.info(alarmService.setupAlarmChan(LoginService.lUserID).toJSon());
        }
    }
}
