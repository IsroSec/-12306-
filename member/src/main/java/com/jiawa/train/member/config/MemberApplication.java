package com.jiawa.train.member.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.lang.reflect.Proxy;

@SpringBootApplication
@ComponentScan("com.jiawa")
@MapperScan("com.jiawa.train.member.mapper")
public class MemberApplication {

    private static final Logger LOG = LoggerFactory.getLogger(MemberApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MemberApplication.class);
        Environment environment = application.run(args).getEnvironment();
        LOG.info("启动成功！");
        LOG.info("MemberApplication: \t http://127.0.0.1:{}{}/", environment.getProperty("server.port"), environment.getProperty("server.servlet.context-path"));
    }

}
