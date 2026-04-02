package com.sarthak.configclientproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Value("${order.message}")
    public String message;

    @GetMapping("/order")
    public String getOrderMessage() {
        return message;
    }
}
