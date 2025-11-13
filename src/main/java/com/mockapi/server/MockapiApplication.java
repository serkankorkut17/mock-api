package com.mockapi.server;

import com.mockapi.server.ui.MockApiFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import javax.swing.*;

@SpringBootApplication
public class MockapiApplication {

	public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext ctx = SpringApplication.run(MockapiApplication.class, args);
        Environment env = ctx.getEnvironment();
        for (String profile : env.getActiveProfiles()) {
            if ("ui".equals(profile)) {
                SwingUtilities.invokeLater(() -> ctx.getBean(MockApiFrame.class).setVisible(true));
                break;
            }
        }
	}

}
