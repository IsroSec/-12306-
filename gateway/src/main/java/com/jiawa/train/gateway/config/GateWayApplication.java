package com.jiawa.train.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.jiawa")
public class GateWayApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GateWayApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GateWayApplication.class);
        Environment environment = application.run(args).getEnvironment();
        LOG.info("启动成功！");
        LOG.info("MemberApplication: \t http://127.0.0.1:{}/", environment.getProperty("server.port"));
    }

}
