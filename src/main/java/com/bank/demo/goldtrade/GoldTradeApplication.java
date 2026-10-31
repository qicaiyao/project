package com.bank.demo.goldtrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 实物黄金交易接口服务启动类
 * <p>
 * HTTP + XML 报文格式，供手机银行前置系统调用。
 */
@SpringBootApplication
public class GoldTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoldTradeApplication.class, args);
    }
}
