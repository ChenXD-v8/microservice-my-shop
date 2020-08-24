package com.funtl.myshop.service.login.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import sun.applet.Main;

@EnableHystrix
@EnableHystrixDashboard
@SpringBootApplication(scanBasePackages = "com.funtl.myshop")
@EnableTransactionManagement
@EnableJpaRepositories("com.funtl.myshop.commons.mapper")
@EntityScan("com.funtl.myshop.commons.domain")
public class MyshopServiceLoginProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyshopServiceLoginProviderApplication.class,args);
        Main.main(args);
    }
}
