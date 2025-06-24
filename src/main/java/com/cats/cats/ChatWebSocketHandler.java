package com.cats.cats;

import com.cats.cats.entities.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler implements ApplicationContextAware {

    private final Map<ObjectId, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        ObjectId userId = new ObjectId(session.getAttributes().get("userId").toString());
        sessions.put(userId, session);
    }

    public void sendMessageToUser(ObjectId userId, ChatMessage message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message.toJson()));
            } catch (IOException e) {
                // Manejar error
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().remove(session);
    }

    // Método para acceso seguro al bean
    public static ChatWebSocketHandler getInstance() {
        return context.getBean(ChatWebSocketHandler.class);
    }
}
