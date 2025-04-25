package com.example.hipreader.domain.chatmessage.websocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.hipreader.common.exception.ErrorCode;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.util.JwtUtil;
import com.example.hipreader.domain.chatmessage.service.ChatMessageService;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private final JwtUtil jwtUtil;
	private final ChatMessageService chatMessageService;
	private final UserRepository userRepository;
	private final Map<String, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		String query = session.getUri().getQuery(); // roomId=1&token=xxx
		Map<String, String> queryMap = Arrays.stream(query.split("&"))
			.map(s -> s.split("="))
			.collect(Collectors.toMap(a -> a[0], a -> a[1]));

		String token = queryMap.get("token");
		String roomId = queryMap.get("roomId");

		String nickname = jwtUtil.extractNickname(token);

		session.getAttributes().put("nickname", nickname);
		session.getAttributes().put("roomId", roomId);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String msg = objectMapper.readTree(message.getPayload()).get("message").asText();
		String nickname = (String)session.getAttributes().get("nickname");
		String roomId = (String)session.getAttributes().get("roomId");

		Long userId = userRepository.findUsesrByNickname(nickname)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND)).getId();
		Long discussionId = Long.parseLong(roomId);

		// DB 에 저장
		chatMessageService.saveMessage(discussionId, userId, msg);

		// 같은 방 유저에게 메시지 전송
		roomSessions.putIfAbsent(roomId, new ArrayList<>());
		List<WebSocketSession> sessions = roomSessions.get(roomId);

		if (!sessions.contains(session)) {
			sessions.add(session);
		}

		for (WebSocketSession s : sessions) {
			if (s.isOpen()) {
				s.sendMessage(new TextMessage(nickname + ": " + msg));
			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		// 모든 방에서 세션 제거
		for (List<WebSocketSession> sessions : roomSessions.values()) {
			sessions.remove(session);
		}
	}
}
