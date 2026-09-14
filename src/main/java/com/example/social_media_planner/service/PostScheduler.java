package com.example.social_media_planner.service;

import com.example.social_media_planner.model.Post;
import com.example.social_media_planner.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PostScheduler {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SocialMediaService socialMediaService;

    @Scheduled(fixedRate = 30000)
    public void processScheduledPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<Post> posts = postRepository.findAll();

        for (Post post : posts) {
            if (post.getStatus() != null && post.getStatus().equalsIgnoreCase("Pending") && post.getScheduledTime() != null) {
                if (post.getScheduledTime().isBefore(now) || post.getScheduledTime().isEqual(now)) {
                    boolean success = socialMediaService.publishPost(post.getPlatform(), post.getCaption());
                    if (success) {
                        post.setStatus("Published");
                        postRepository.save(post);
                    }
                }
            }
        }
    }
}