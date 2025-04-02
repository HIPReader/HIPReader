package com.example.heapreader.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.heapreader.domain.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
	Page<Post> findAllPosts(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.id = :postId AND p.deletedAt IS NULL")
	Optional<Post> findById(@Param("postId") Long postId);
}
