package com.example.hipreader.domain.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hipreader.domain.user.dto.response.GetUserResponseDto;
import com.example.hipreader.domain.user.entity.User;

import io.lettuce.core.dynamic.annotation.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsUserByEmail(String email);
	Optional<User> findByEmail(String email);
	Optional<User> findUserById(Long userId);

	@Query("SELECT u FROM User u WHERE u.id = :userId AND u.deletedAt IS NULL")
	Optional<User> findActiveUserById(@Param("userId") Long userId);

	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
	Page<User> findAllActiveUsers(Pageable pageable);
}
