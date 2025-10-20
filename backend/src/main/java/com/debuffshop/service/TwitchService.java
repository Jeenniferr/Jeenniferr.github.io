package com.example.debuffshop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Service
public class TwitchService {

    private final String clientId = System.getenv("TWITCH_CLIENT_ID");
    private final String clientSecret = System.getenv("TWITCH_CLIENT_SECRET");
    private String appToken = null;

    public boolean isStreamerLive(String username) {
        if (appToken == null) appToken = getAppToken();
        String url = "https://api.twitch.tv/helix/streams?user_login=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-ID", clientId);
        headers.set("Authorization", "Bearer " + appToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate rest = new RestTemplate();
        ResponseEntity<Map> response = rest.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getBody() == null) return false;
        var data = (java.util.List<?>) response.getBody().get("data");
        return data != null && !data.isEmpty();
    }

    private String getAppToken() {
        RestTemplate rest = new RestTemplate();
        String url = "https://id.twitch.tv/oauth2/token?client_id=" + clientId +
                     "&client_secret=" + clientSecret +
                     "&grant_type=client_credentials";
        Map<?, ?> result = rest.postForObject(url, null, Map.class);
        return result != null ? (String) result.get("access_token") : null;
    }
}