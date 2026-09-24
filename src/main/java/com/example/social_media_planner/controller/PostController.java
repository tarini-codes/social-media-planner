package com.example.social_media_planner.controller;

import com.example.social_media_planner.model.Post;
import com.example.social_media_planner.model.Users;
import com.example.social_media_planner.repository.PostRepository;
import com.example.social_media_planner.repository.UserRepository;
import com.example.social_media_planner.service.SocialMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocialMediaService socialMediaService;

    private Users getLoggedInUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName());
    }

    @GetMapping({"/", "/posts"})
    public String viewHomePage(@RequestParam(value = "platform", required = false) String platform,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size,
                               Authentication authentication,
                               Model model) {

        Users currentUser = getLoggedInUser(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledTime").descending());
        Page<Post> postPage;

        if (platform != null && !platform.isEmpty() && !platform.equals("All")) {
            postPage = postRepository.findAllByOwnerAndPlatform(currentUser, platform, pageable);
        } else {
            postPage = postRepository.findAllByOwner(currentUser, pageable);
        }

        model.addAttribute("post", new Post());
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("selectedPlatform", platform);
        model.addAttribute("username", currentUser.getUsername());

        return "posts";
    }

    @GetMapping("/posts/publish/{id}")
    public String publishPostNow(@PathVariable("id") Long id, Authentication authentication) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No post id: " + id));

        checkOwnership(post, authentication);

        boolean success = socialMediaService.publishPost(post.getPlatform(), post.getCaption());
        if (success) {
            post.setStatus("Published");
            postRepository.save(post);
        }

        return "redirect:/posts";
    }

    @PostMapping("/posts")
    public String savePost(@ModelAttribute("post") Post post, Authentication authentication) {
        if (post.getId() != null) {
            Post existing = postRepository.findById(post.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid post Id"));
            checkOwnership(existing, authentication);
            post.setOwner(existing.getOwner());
        } else {
            post.setOwner(getLoggedInUser(authentication));
        }

        if (post.getStatus() == null || post.getStatus().isEmpty()) {
            post.setStatus("Pending");
        }

        postRepository.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable("id") Long id, Authentication authentication) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No post id: " + id));
        checkOwnership(post, authentication);
        postRepository.deleteById(id);
        return "redirect:/posts";
    }

    @GetMapping("/posts/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Authentication authentication, Model model) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        checkOwnership(post, authentication);

        Users currentUser = getLoggedInUser(authentication);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("scheduledTime").descending());
        Page<Post> postPage = postRepository.findAllByOwner(currentUser, pageable);

        model.addAttribute("post", post);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("selectedPlatform", "All");
        model.addAttribute("username", currentUser.getUsername());

        return "posts";
    }

    private void checkOwnership(Post post, Authentication authentication) {
        Users currentUser = getLoggedInUser(authentication);
        if (!post.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("You do not have permission to access this post");
        }
    }
}