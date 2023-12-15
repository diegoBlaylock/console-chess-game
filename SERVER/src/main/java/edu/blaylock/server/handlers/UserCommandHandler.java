package edu.blaylock.server.handlers;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.userCommands.UserGameCommand;

public interface UserCommandHandler<T extends UserGameCommand> {
    default void handleGeneric(UserGameCommand command, Session session) throws Exception {
        handle((T) command, session);
    }

    void handle(T command, Session session) throws Exception;
}
