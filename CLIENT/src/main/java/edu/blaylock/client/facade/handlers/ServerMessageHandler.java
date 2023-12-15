package edu.blaylock.client.facade.handlers;

import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageHandler<T extends ServerMessage> {
    /**
     * Used to handle generic messages without the pain of using Java Reflection which isn't possible with generics.
     * Simply calls the handleMessage method after casting the argument.
     *
     * @param message Message to handle
     */
    @SuppressWarnings("unchecked")
    default void handleGeneric(ServerMessage message) {
        handleMessage((T) message);
    }

    void handleMessage(T message);
}
