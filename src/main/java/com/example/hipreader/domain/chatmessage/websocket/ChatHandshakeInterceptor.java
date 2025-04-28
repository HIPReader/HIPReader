package com.example.hipreader.domain.chatmessage.websocket;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.discussion.status.Status;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

	private final DiscussionRepository discussionRepository;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {
		String query = request.getURI().getQuery();
		if (query != null && query.startsWith("roomId=")) {
			Map<String, String> queryMap = Arrays.stream(query.split("&"))
				.map(s -> s.split("="))
				.collect(Collectors.toMap(a -> a[0], a -> a[1]));

			String token = queryMap.get("token");
			String roomId = queryMap.get("roomId");

			// discussion 이 없는 경우
			Long discussionId = Long.parseLong(roomId);
			Optional<Discussion> discussionOptional = discussionRepository.findById(discussionId);
			if (discussionOptional.isEmpty())
				return false;

			Discussion discussion = discussionOptional.get();

			// 상태가 ACITVE 가 아닌 경우
			return discussion.getStatus() == Status.ACTIVE;
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {

	}
}
