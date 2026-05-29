package org.laundry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.cache.annotation.EnableCaching
public class LaundryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LaundryApplication.class, args);
    }
}
