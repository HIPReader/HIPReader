package com.example.hipreader.domain.refreshtoken.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.hipreader.domain.refreshtoken.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByRefreshToken(String token);

	Optional<RefreshToken> findByUserId(Long userId);
}
