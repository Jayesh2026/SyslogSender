package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SyslogSenderApplication {

	// private final SyslogSenderService syslogSenderService;


	// public SyslogSenderApplication(SyslogSenderService syslogSenderService) {
    //     this.syslogSenderService = syslogSenderService;
    // }

    public static void main(String[] args) {
        SpringApplication.run(SyslogSenderApplication.class, args);
    }

    // @Override
    // public void run(String... args) throws Exception {
    //     // Send a test syslog message on startup
    //     syslogSenderService.sendTestSyslogMessage();
    // }


}
