package com.example.hipreader.domain.userdiscussion.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.common.exception.ErrorCode;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.userdiscussion.dto.response.NotificationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final UserRepository userRepository;
	private final EmailService emailService;

	// 신청 알림
	public void sendApplyAlert(NotificationMessage message) {
		// 예: 이메일, 푸시, SMS 등으로 알림 전송
		log.info("신청 알림: userId={}, discussionId={}, eventTime={}",
			message.userId(), message.discussionId(), message.eventTime());

		User user = userRepository.findById(message.userId()).orElseThrow(()-> new NotFoundException(USER_NOT_FOUND));

		String content = String.format("안녕하세요, %s님! %d번 토론방에 신청하셨습니다.",
			user.getNickname(), message.discussionId());

		//메일 전송
		emailService.sendSimpleEmail(
			user.getEmail(),
			"[힙리더] 토론방 신청이 접수되었습니다.",
			content);
	}

	// 승인 알림
	@Transactional
	public void sendApprovalAlert(NotificationMessage message) {
		log.info("승인 알림: userId={}, discussionId={}, eventTime={}",
			message.userId(), message.discussionId(), message.eventTime());

		User user = userRepository.findById(message.userId()).orElseThrow(()-> new NotFoundException(USER_NOT_FOUND));

		// 메일 전송
		emailService.sendSimpleEmail(
			user.getEmail(),
			"[힙리더] 토론방 참가 승인",
			user.getNickname() + "님의 신청이 승인되었습니다."
		);

	}

	// 거절 알림
	public void sendRejectionAlert(NotificationMessage message) {
		log.info("거절 알림: userId={}, discussionId={}, eventTime={}",
			message.userId(), message.discussionId(), message.eventTime());

		User user = userRepository.findById(message.userId()).orElseThrow(()-> new NotFoundException(USER_NOT_FOUND));
		// 메일 전송
		emailService.sendSimpleEmail(
			user.getEmail(),
			"[힙리더] 토론방 참가 거절",
			user.getNickname() + "님의 신청이 거절되었습니다."
		);
	}
}
