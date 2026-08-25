package com.zilpher.webdev2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shop")
public class ShopProperties {
    private String name;
    private String currency;

    public ShopProperties() {}

    public String getName() {
        return name;
    }public String getCurrency() {
        return currency;
    }

    public void setName(String name) {
        this.name = name;
    }public void setCurrency(String currency) {
        this.currency = currency;
    }
}