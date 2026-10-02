package com.example.test;

import javax.swing.SwingUtilities;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.test.swing.MainFrame;

@SpringBootApplication
public class TestApplication {

	public static void main(String[] args) {
		// SpringApplication.run(TestApplication.class, args);

		
		ConfigurableApplicationContext context = new SpringApplicationBuilder(TestApplication.class).headless(false)
				.run(args);
		
		SwingUtilities.invokeLater(() -> {
			MainFrame frame = context.getBean(MainFrame.class);
			frame.setVisible(true);
		});
	}

}
