package com.example.hipreader.domain.chatmessage.entity;

import java.time.LocalDateTime;

import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_messages")
@NoArgsConstructor
public class ChatMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discussion_id")
	private Discussion discussion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String message;

	private LocalDateTime createdAt = LocalDateTime.now();

	@Builder
	public ChatMessage(Discussion discussion, User user, String message, LocalDateTime createdAt) {
		this.discussion = discussion;
		this.user = user;
		this.message = message;
		this.createdAt = createdAt;
	}
}
