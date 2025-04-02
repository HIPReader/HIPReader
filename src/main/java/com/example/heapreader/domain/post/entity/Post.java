package com.example.heapreader.domain.post.entity;

import com.example.heapreader.common.entity.TimeStamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "posts")
public class Post extends TimeStamped {
	//제목, 내용,조회수, 좋아요 수
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String title;
	private String content;
	private Integer viewCount;
	private Integer likeCount;

}
