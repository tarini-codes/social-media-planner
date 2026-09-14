package com.example.social_media_planner.controller;

import com.example.social_media_planner.service.SocialMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.social_media_planner.model.Post;
import com.example.social_media_planner.repository.PostRepository;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SocialMediaService socialMediaService;

    @GetMapping({"/", "/posts"})
    public String viewHomePage(@RequestParam(value = "platform", required = false) String platform,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledTime").descending());
        Page<Post> postPage;

        if (platform != null && !platform.isEmpty() && !platform.equals("All")) {
            postPage = postRepository.findAllByPlatform(platform, pageable);
        } else {
            postPage = postRepository.findAll(pageable);
        }

        model.addAttribute("post", new Post());
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("selectedPlatform", platform);

        return "posts";
    }

    @GetMapping("/posts/publish/{id}")
    public String publishPostNow(@PathVariable("id") Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No post id: " + id));
        boolean success = socialMediaService.publishPost(post.getPlatform(), post.getCaption());
        if (success) {
            post.setStatus("Published");
            postRepository.save(post);
        }

        return "redirect:/posts";
    }

    @PostMapping("/posts")
    public String savePost(@ModelAttribute("post") Post post) {
        if (post.getStatus() == null || post.getStatus().isEmpty()) {
            post.setStatus("Pending");
        }

        postRepository.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        postRepository.deleteById(id);
        return "redirect:/posts";
    }

    @GetMapping("/posts/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        model.addAttribute("post", post);
        model.addAttribute("posts", postRepository.findAll());
        return "posts";
    }
}