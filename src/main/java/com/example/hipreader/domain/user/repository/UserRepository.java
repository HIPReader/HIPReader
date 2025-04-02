package com.example.hipreader.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hipreader.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsUserByEmail(String email);
	Optional<User> findByEmail(String email);
	Optional<User> findUserById(Long userId);
}
