package com.xiaoliu.demo.util;

import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import org.springframework.stereotype.Component;

/**
 * @author xiaoliu
 * @date 2019/11/26 9:34
 */
@Component
public class SuccessRuuner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        System.out.println("恭喜你，软件成功运行了");
    }
}
