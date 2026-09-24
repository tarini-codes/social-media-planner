package com.example.social_media_planner.repository;

import com.example.social_media_planner.model.Post;
import com.example.social_media_planner.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByPlatform(String platform);

    Page<Post> findAllByOwner(Users owner, Pageable pageable);
    Page<Post> findAllByOwnerAndPlatform(Users owner, String platform, Pageable pageable);
}