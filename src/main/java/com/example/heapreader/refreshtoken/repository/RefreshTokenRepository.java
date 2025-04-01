package com.example.heapreader.refreshtoken.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.heapreader.refreshtoken.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
