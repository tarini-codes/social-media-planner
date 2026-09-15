package com.example.social_media_planner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SocialMediaService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final ThreadPoolTaskScheduler taskScheduler;

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Value("${telegram.chat.id}")
    private String telegramChatId;

    @Value("${twitter.api.key}")
    private String twitterApiKey;

    @Value("${twitter.api.secret}")
    private String twitterApiSecret;

    @Value("${twitter.access.token}")
    private String twitterAccessToken;

    @Value("${twitter.access.token.secret}")
    private String twitterAccessTokenSecret;

    @Value("${linkedin.access.token}")
    private String linkedinAccessToken;

    @Value("${linkedin.person.urn}")
    private String linkedinPersonUrn;

    public SocialMediaService(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void schedulePost(String platform, String caption, LocalDateTime scheduledTime) {
        Runnable task = () -> {
            boolean success = publishPost(platform, caption);
            if (success) {
                System.out.println("Successfully auto-published to " + platform + " at " + LocalDateTime.now());
            } else {
                System.out.println("Failed to auto-publish to " + platform);
            }
        };

            Date executionTime = Date.from(scheduledTime.atZone(ZoneId.systemDefault()).toInstant());

        taskScheduler.schedule(task, executionTime);
        System.out.println("Post successfully scheduled for: " + scheduledTime);
    }

    public boolean publishPost(String platform, String caption) {
        try {
            switch (platform.toLowerCase()) {
                case "telegram":
                    return publishToTelegram(caption);
                case "twitter":
                    return publishToTwitter(caption);
                case "linkedin":
                    return publishToLinkedIn(caption);
                case "instagram":
                case "facebook":
                case "youtube":
                    return publishMockPlatform(platform, caption);
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

        restTemplate.postForObject(url, payload, String.class);
        return true;
    }

    private boolean publishMockPlatform(String platform, String caption) {
        System.out.println("Simulated auto-publish success for platform: " + platform + " with caption: " + caption);
        return true;
    }

    private boolean publishToTwitter(String caption) {
        String url = "https://api.twitter.com/2/tweets";

        String authHeader = TwitterOAuthUtil.buildAuthorizationHeader(
                "POST", url,
                twitterApiKey, twitterApiSecret,
                twitterAccessToken, twitterAccessTokenSecret
        );

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("text", caption);

        org.springframework.http.HttpEntity<Map<String, String>> request =
                new org.springframework.http.HttpEntity<>(body, headers);

        org.springframework.http.ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        return response.getStatusCode().is2xxSuccessful();
    }

    private boolean publishToLinkedIn(String caption) {
        String url = "https://api.linkedin.com/v2/ugcPosts";

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + linkedinAccessToken);
        headers.set("X-Restli-Protocol-Version", "2.0.0");
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        Map<String, Object> shareContent = new HashMap<>();
        Map<String, Object> shareCommentary = new HashMap<>();
        shareCommentary.put("text", caption);

        Map<String, Object> media = new HashMap<>();
        media.put("shareCommentary", shareCommentary);
        media.put("shareMediaCategory", "NONE");

        Map<String, Object> specificContent = new HashMap<>();
        specificContent.put("com.linkedin.ugc.ShareContent", media);

        Map<String, Object> body = new HashMap<>();
        body.put("author", linkedinPersonUrn);
        body.put("lifecycleState", "PUBLISHED");
        body.put("specificContent", specificContent);

        Map<String, Object> visibility = new HashMap<>();
        visibility.put("com.linkedin.ugc.MemberNetworkVisibility", "PUBLIC");
        body.put("visibility", visibility);

        org.springframework.http.HttpEntity<Map<String, Object>> request =
                new org.springframework.http.HttpEntity<>(body, headers);

        org.springframework.http.ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        return response.getStatusCode().is2xxSuccessful();
    }
}