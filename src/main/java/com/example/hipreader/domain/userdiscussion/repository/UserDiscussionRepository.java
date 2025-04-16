package com.example.hipreader.domain.userdiscussion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.userdiscussion.ApplicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;

public interface UserDiscussionRepository extends JpaRepository<UserDiscussion, Long> {
	boolean existsByUserAndDiscussion(User user, Discussion discussion);

	List<UserDiscussion> findByDiscussion(Discussion discussion);

	List<UserDiscussion> findByUser(User user);

	long countByDiscussionAndStatus(Discussion discussion, ApplicationStatus applicationStatus);

	boolean existsByUserIdAndDiscussionId(Long userId, Long discussionId);
}
