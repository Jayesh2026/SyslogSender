package com.example.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;

@Service
public class SyslogSenderService {

    private static final Logger logger = LoggerFactory.getLogger(SyslogSenderService.class);

    private final TcpSyslogMessageSender messageSender;

    public SyslogSenderService(@Value("${spring.application.name:default-app}") String appName,
                                @Value("${syslog.server.hostname}") String syslogServerHostname,
                                @Value("${syslog.server.port}") int syslogServerPort) {
        messageSender = new TcpSyslogMessageSender();

        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            messageSender.setDefaultMessageHostname(localHostName);
        } catch (UnknownHostException e) {
            messageSender.setDefaultMessageHostname("localhost");
        }

        messageSender.setDefaultAppName(appName);
        messageSender.setSyslogServerHostname(syslogServerHostname);
        messageSender.setSyslogServerPort(syslogServerPort);
        messageSender.setDefaultFacility(Facility.USER);
        messageSender.setDefaultSeverity(Severity.INFORMATIONAL);
        messageSender.setMessageFormat(MessageFormat.RFC_5424);
        messageSender.setSsl(false);
    }

    public void sendTestSyslogMessage() {
        try {
            messageSender.sendMessage("This is a test message sent from Spring Boot!");
            logger.info("Test syslog message sent.");
        } catch (Exception e) {
            logger.error("Failed to send syslog message: {}", e.getMessage(), e);
        }
    }
}
