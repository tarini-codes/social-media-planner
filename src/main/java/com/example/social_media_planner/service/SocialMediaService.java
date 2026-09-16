package com.example.social_media_planner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SocialMediaService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Value("${telegram.chat.id}")
    private String telegramChatId;

    public boolean publishPost(String platform, String caption) {
        try {
            switch (platform.toLowerCase()) {
                case "telegram":
                    return publishToTelegram(caption);
                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean publishToTelegram(String caption) {
        String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";

        Map<String, String> payload = new HashMap<>();
        payload.put("chat_id", telegramChatId);
        payload.put("text", "Scheduled Auto-Post:\n\n" + caption);

        ResponseEntity<String> response = restTemplate.postForEntity(url, payload, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }
}