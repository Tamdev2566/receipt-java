package com.receipt.receiptPhase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ReceiptApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReceiptApplication.class, args);
    }
}

//package com.receipt.receiptPhase;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class ReceiptApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(ReceiptApplication.class, args);
//    }
//}