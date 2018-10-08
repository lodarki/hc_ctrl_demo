package com.ts.hc_ctrl_demo.common.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.FileNotFoundException;

@Configuration
public class MultipartConfig {

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String location = "D:/Tomcat-File/tmp";
        try {
            String path = ResourceUtils.getURL("classpath:").getPath();
            File file = new File(path);
            location = file.getAbsolutePath() + File.separator + "upload/images/";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File locationDir = new File(location);
        if (!locationDir.exists()) {
            locationDir.mkdirs();
        }
        factory.setLocation(location);
        return factory.createMultipartConfig();
    }
}
