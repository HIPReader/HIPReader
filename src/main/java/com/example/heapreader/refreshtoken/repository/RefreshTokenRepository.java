package com.example.heapreader.refreshtoken.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.heapreader.refreshtoken.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByUserId(Long userId);
}
