package com.example.heapreader.domain.refreshtoken.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.heapreader.domain.refreshtoken.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByUserId(Long userId);
}
