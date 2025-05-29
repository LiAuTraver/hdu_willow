package com.hdu.vboard;

import com.hdu.hdufpga.config.WebConfiguration;
import com.hdu.hdufpga.util.RedisUtil;
import hdu.svccmn.UserStatisticServiceImpl;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// FIXME: springboot cannot find bean vvvvvv
@Import({WebConfiguration.class, RedisUtil.class, UserStatisticServiceImpl.class})
@SpringBootApplication
@EnableTransactionManagement
@EnableDubbo
@EnableAsync
public class VirtualBoardApplication {
  public static void main(String[] args) {
    SpringApplication.run(VirtualBoardApplication.class, args);
  }
}