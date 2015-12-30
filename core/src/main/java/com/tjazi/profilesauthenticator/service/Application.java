package com.tjazi.profilesauthenticator.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Krzysztof Wasiak on 08/11/2015.
 */

@SpringBootApplication
@EnableHystrix
public class Application {

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }
}
