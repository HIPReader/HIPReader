package com.example.heapreader.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.heapreader.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
