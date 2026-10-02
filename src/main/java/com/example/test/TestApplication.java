package com.example.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);

		
		/*ConfigurableApplicationContext context = new SpringApplicationBuilder(TestApplication.class).headless(false)
				.run(args);
		
		SwingUtilities.invokeLater(() -> {
			MainFrame frame = context.getBean(MainFrame.class);
			frame.setVisible(true);
		});*/
		
	

	    
	}

}
