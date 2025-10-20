package com.example.debuffshop.controller;

import com.example.debuffshop.model.User;
import com.example.debuffshop.repository.UserRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired private UserRepository userRepo;

    @PostMapping("/create-checkout")
    public ResponseEntity<Map<String, String>> createCheckout(@RequestParam Long userId, @RequestParam int coins) throws Exception {
        int amountCents = (coins / 10) * 100; // Example: 100 coins = $1
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://yourdomain.com/success.html")
                .setCancelUrl("https://yourdomain.com/cancel.html")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount((long) amountCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(coins + " Debuff Coins")
                                                                .build()
                                                ).build()
                                ).build()
                )
                .build();

        Session session = Session.create(params);
        Map<String, String> response = new HashMap<>();
        response.put("url", session.getUrl());
        return ResponseEntity.ok(response);
    }

    // Webhook to credit coins
    @PostMapping("/webhook")
    public ResponseEntity<String> stripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sig) {
        // Parse event here using Stripe’s library (e.g., coin purchase success)
        // On success:
        // User user = userRepo.findById(userId).get();
        // user.setCoins(user.getCoins() + coinsPurchased);
        // userRepo.save(user);
        return ResponseEntity.ok("ok");
    }
}