package com.example.hipreader.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.hipreader.domain.chatmessage.websocket.ChatHandshakeInterceptor;
import com.example.hipreader.domain.chatmessage.websocket.ChatWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	private final ChatWebSocketHandler chatWebSocketHandler;
	private final ChatHandshakeInterceptor chatHandshakeInterceptor;

	public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler,
		ChatHandshakeInterceptor chatHandshakeInterceptor) {
		this.chatWebSocketHandler = chatWebSocketHandler;
		this.chatHandshakeInterceptor = chatHandshakeInterceptor;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(chatWebSocketHandler, "/ws/chat")
			.addInterceptors(chatHandshakeInterceptor)
			.setAllowedOrigins("*");
	}
}
