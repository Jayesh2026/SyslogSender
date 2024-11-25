package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.SyslogSenderService;

@RestController
public class SyslogSenderController {

    private final SyslogSenderService syslogSenderService;

    public SyslogSenderController(SyslogSenderService syslogSenderService) {
        this.syslogSenderService = syslogSenderService;
    }

    @GetMapping("/send-syslog")
    public String sendSyslogMessage() {
        syslogSenderService.sendTestSyslogMessage();
        return "Syslog message sent!";
    }
}
