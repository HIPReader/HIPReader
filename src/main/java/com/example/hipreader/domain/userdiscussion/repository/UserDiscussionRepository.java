package com.example.hipreader.domain.userdiscussion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;

import io.lettuce.core.dynamic.annotation.Param;

public interface UserDiscussionRepository extends JpaRepository<UserDiscussion, Long> {
	boolean existsByUserAndDiscussion(User user, Discussion discussion);

	@Query("SELECT ud FROM UserDiscussion ud JOIN FETCH ud.user WHERE ud.discussion = :discussion")
	List<UserDiscussion> findByDiscussionWithUser(@Param("discussion") Discussion discussion);

	@Query("SELECT ud FROM UserDiscussion ud JOIN FETCH ud.discussion WHERE ud.user = :user")
	List<UserDiscussion> findByUserWithDiscussion(@Param("user") User user);

	boolean existsByUserIdAndDiscussionIdAndStatus(Long userId, Long discussionId, ApplicationStatus applicationStatus);
}
