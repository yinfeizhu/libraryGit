package org.fly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling//开启定时任务的注解
@SpringBootApplication
@ServletComponentScan//开启了@WebServlet注解的扫描,拦截请求需要，无论是过滤器还是拦截器都需要加此注解
public class LibraryManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }

}
