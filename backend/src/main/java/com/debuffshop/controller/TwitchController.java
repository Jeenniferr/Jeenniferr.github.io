package com.example.debuffshop.controller;

import com.example.debuffshop.service.TwitchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/twitch")
@CrossOrigin(origins = "*")
public class TwitchController {

    private final TwitchService twitchService;
    public TwitchController(TwitchService twitchService) { this.twitchService = twitchService; }

    @GetMapping("/live/{username}")
    public boolean isLive(@PathVariable String username) {
        return twitchService.isStreamerLive(username);
    }
}