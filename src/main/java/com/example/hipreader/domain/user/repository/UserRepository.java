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

	@Query("select new com.example.hipreader.domain.user.dto.response.GetUserResponseDto(u.id, u.email, u.age, u.gender) from User u where u.id = :userId")
	Optional<GetUserResponseDto> findUserDtoById(@Param("userId") Long userId);

	@Query("select new com.example.hipreader.domain.user.dto.response.GetUserResponseDto(u.id, u.email, u.age, u.gender) from User u")
	Page<GetUserResponseDto> findAllUserDto(Pageable pageable);
}
