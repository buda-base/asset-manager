package io.bdrc.assetmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication ==  @EnableAutoConfiguration + @ComponentScan + @SpringBootConfiguration
@SpringBootApplication
public class ReactAndSpringAssetManagerApplication {

   /* private */ public static void main(String[] args) {
        SpringApplication.run(ReactAndSpringAssetManagerApplication.class, args);
    }
}