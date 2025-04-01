package com.example.heapreader.auth.service;

import org.springframework.stereotype.Service;

import com.example.heapreader.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;

}
