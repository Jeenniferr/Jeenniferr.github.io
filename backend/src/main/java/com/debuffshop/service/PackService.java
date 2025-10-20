package com.example.debuffshop.service;

import com.example.debuffshop.model.*;
import com.example.debuffshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PackService {

    private static final int PACK_COST = 100;

    @Autowired private UserRepository userRepo;
    @Autowired private CardTypeRepository cardTypeRepo;
    @Autowired private CardRepository cardRepo;

    public boolean buyPacks(Long userId, int count) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return false;

        int total = PACK_COST * count;
        if (user.getCoins() < total) return false;

        user.setCoins(user.getCoins() - total);
        user.setPacks(user.getPacks() + count);
        userRepo.save(user);
        return true;
    }

    public List<Card> openPack(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null || user.getPacks() <= 0) return null;

        user.setPacks(user.getPacks() - 1);
        userRepo.save(user);

        List<CardType> allTypes = cardTypeRepo.findAll();
        List<Card> opened = new ArrayList<>();

        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            CardType type = allTypes.get(rand.nextInt(allTypes.size()));
            Card card = new Card();
            card.setUser(user);
            card.setType(type);
            cardRepo.save(card);
            opened.add(card);
        }

        return opened;
    }
}