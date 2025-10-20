package com.example.debuffshop.config;

import com.stripe.Stripe;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {
    @PostConstruct
    public void setup() {
        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");
    }
}