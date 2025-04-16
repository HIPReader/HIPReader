package com.example.hipreader.auth.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.hipreader.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByRefreshToken(String token);

	void deleteByUserId(Long userId);
}
