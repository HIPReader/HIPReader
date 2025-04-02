package com.example.heapreader.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.heapreader.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
