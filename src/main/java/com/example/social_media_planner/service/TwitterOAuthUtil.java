package com.example.social_media_planner.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

public class TwitterOAuthUtil {

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String buildAuthorizationHeader(
            String httpMethod, String url,
            String apiKey, String apiSecret,
            String accessToken, String accessTokenSecret) {

        String nonce = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);

        Map<String, String> oauthParams = new TreeMap<>();
        oauthParams.put("oauth_consumer_key", apiKey);
        oauthParams.put("oauth_nonce", nonce);
        oauthParams.put("oauth_signature_method", "HMAC-SHA1");
        oauthParams.put("oauth_timestamp", timestamp);
        oauthParams.put("oauth_token", accessToken);
        oauthParams.put("oauth_version", "1.0");

        StringBuilder paramString = new StringBuilder();
        for (Map.Entry<String, String> entry : oauthParams.entrySet()) {
            if (paramString.length() > 0) paramString.append("&");
            paramString.append(encode(entry.getKey())).append("=").append(encode(entry.getValue()));
        }

        String baseString = httpMethod.toUpperCase() + "&" + encode(url) + "&" + encode(paramString.toString());
        String signingKey = encode(apiSecret) + "&" + encode(accessTokenSecret);

        String signature;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] rawSignature = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
            signature = Base64.getEncoder().encodeToString(rawSignature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        oauthParams.put("oauth_signature", signature);

        StringBuilder header = new StringBuilder("OAuth ");
        int i = 0;
        for (Map.Entry<String, String> entry : oauthParams.entrySet()) {
            if (i++ > 0) header.append(", ");
            header.append(encode(entry.getKey())).append("=\"").append(encode(entry.getValue())).append("\"");
        }

        return header.toString();
    }
}