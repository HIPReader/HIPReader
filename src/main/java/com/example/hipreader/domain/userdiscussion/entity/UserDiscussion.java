package com.example.hipreader.domain.userdiscussion.entity;

import java.time.LocalDateTime;

import com.example.hipreader.common.entity.TimeStamped;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Setter
@Getter
@Table(name = "user_discussions")
public class UserDiscussion extends TimeStamped {

	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discussion_id", nullable = false)
	private Discussion discussion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ApplicationStatus status;

	@Column(nullable = false)
	private LocalDateTime appliedAt;

	@Column
	private LocalDateTime statusUpdatedAt;


}
