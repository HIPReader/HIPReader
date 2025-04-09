package com.example.hipreader.domain.discussion.repository;

import com.example.hipreader.domain.discussion.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
}
