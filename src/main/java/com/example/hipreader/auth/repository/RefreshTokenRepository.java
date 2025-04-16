package com.example.hipreader.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.hipreader.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
	void deleteByUserId(Long userId);
}
