package com.example.hipreader.domain.chatmessage.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.domain.chatmessage.dto.response.GetChatMessageResponseDto;
import com.example.hipreader.domain.chatmessage.entity.ChatMessage;
import com.example.hipreader.domain.chatmessage.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatMessageController {

	private final ChatMessageService chatMessageService;

	@GetMapping("/v1/chat/history")
	public ResponseEntity<List<GetChatMessageResponseDto>> getChatMessageHistory(@RequestParam Long discussionId) {
		List<GetChatMessageResponseDto> responseDtoList = chatMessageService.getChatMessageHistory(discussionId);
		return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
	}
}
