package tests.serverfacade;

import edu.blaylock.client.facade.GameConnection;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.ArrayDeque;
import java.util.Queue;

public class Utils {

    public static class Buffer {
        Queue<ServerMessage> messages = new ArrayDeque<>();

        public Buffer(GameConnection connection) {
            for (ServerMessage.ServerMessageType type : ServerMessage.ServerMessageType.values()) {
                connection.register(type, this::handleMessage);
            }
        }

        synchronized void handleMessage(ServerMessage message) {
            messages.add(message);
        }

        synchronized ServerMessage getMessage() {
            if (messages.isEmpty()) return null;
            return messages.remove();
        }


    }
}
