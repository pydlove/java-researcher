
package com.aiocloud.testservice.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);

        // Echo the message back to the client
        session.sendMessage(new TextMessage("Echo: " + payload));
    }
}