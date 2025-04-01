package com.example.heapreader.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.heapreader.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findUserById(Long userId);
}
