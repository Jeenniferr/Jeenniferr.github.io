package com.example.debuffshop.controller;

import com.example.debuffshop.model.*;
import com.example.debuffshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packs")
@CrossOrigin(origins = "*")
public class PackController {

    @Autowired
    private PackService packService;

    @PostMapping("/buy")
    public ResponseEntity<String> buyPack(@RequestParam Long userId, @RequestParam int count) {
        boolean success = packService.buyPacks(userId, count);
        return success ? ResponseEntity.ok("Purchase successful!") :
                ResponseEntity.badRequest().body("Not enough coins.");
    }

    @PostMapping("/open")
    public ResponseEntity<List<Card>> openPack(@RequestParam Long userId) {
        List<Card> opened = packService.openPack(userId);
        if (opened == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(opened);
    }
}