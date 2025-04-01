package com.example.heapreader.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.heapreader.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
