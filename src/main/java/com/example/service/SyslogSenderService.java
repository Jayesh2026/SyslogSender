package com.example.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;

@Service
public class SyslogSenderService {

    private final TcpSyslogMessageSender messageSender;

    public SyslogSenderService(@Value("${spring.application.name:default-app}") String appName, Environment environment) {
        messageSender = new TcpSyslogMessageSender();

        // Dynamically set hostname
        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            messageSender.setDefaultMessageHostname(localHostName); // Set local hostname
        } catch (UnknownHostException e) {
            e.printStackTrace();
            messageSender.setDefaultMessageHostname("localhost"); // Fallback
        }

        // Set application name dynamically
        messageSender.setDefaultAppName(appName); // Fetched from 'spring.application.name'

        // Other configurations
        messageSender.setDefaultFacility(Facility.USER);
        messageSender.setDefaultSeverity(Severity.INFORMATIONAL);
        // messageSender.setSyslogServerHostname("0.0.0.0"); // Syslog server hostname for locally
        messageSender.setSyslogServerHostname("otel-collector"); // OTEL Collector hostname
        messageSender.setSyslogServerPort(514); // Syslog server port
        messageSender.setMessageFormat(MessageFormat.RFC_5424);
        messageSender.setSsl(false); // Set SSL if needed
    }

    public void sendTestSyslogMessage() {
        try {
            messageSender.sendMessage("This is a test message sent from Spring Boot!");
            System.out.println("Test syslog message sent.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send syslog message: " + e.getMessage());
        }
    }
}
