package org.cczzrs.touch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

// @ComponentScan("org.cczzrs")
@ComponentScan({"org.cczzrs.core","org.cczzrs.touch"})
@ServletComponentScan({"org.cczzrs.touch"})
// @ServletComponentScan({"org.cczzrs.core.sureness","org.cczzrs.touch"}) // 注册权限框架-sureness
@SpringBootApplication
public class TouchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TouchApplication.class, args);
	}

}
