package org.cczzrs.touch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan({"org.cczzrs.core","org.cczzrs.touch"})
@SpringBootApplication
public class TouchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TouchApplication.class, args);
	}

}
