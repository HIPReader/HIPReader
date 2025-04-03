package com.example.hipreader.domain.review.entity;

import com.example.hipreader.common.entity.TimeStamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reviews")
public class Review extends TimeStamped {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String content;
	private Integer rating;

	public void updateContent(String content) {
		this.content = content;
	}

	public void updateRating(Integer rating) {
		this.rating = rating;
	}
}
