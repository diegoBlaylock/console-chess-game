package edu.blaylock.server.handlers;

import chess.ChessGame;
import edu.blaylock.server.database.models.AuthToken;
import edu.blaylock.server.database.models.Game;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.exceptions.SocketException;
import edu.blaylock.server.gameplay.GameManager;
import edu.blaylock.server.gameplay.SessionInfo;
import edu.blaylock.server.gameplay.WSUtils;
import edu.blaylock.utils.gson.GsonUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Spark;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;

/**
 * Contains the all the base handlers for UserGameCommands sent by a client through websockets.
 */
@WebSocket
public class WSHandlers {
    private final UserCommandHandler<?>[] handlers = new UserCommandHandler<?>[UserGameCommand.CommandType.values().length];

    /**
     * Register all handlers
     */
    public void registerHandlers() {
        Spark.webSocket("/connect", this);
        register(UserGameCommand.CommandType.JOIN_PLAYER, (UserCommandHandler<JoinPlayerCommand>) this::handleJoinPlayer);
        register(UserGameCommand.CommandType.JOIN_OBSERVER, (UserCommandHandler<JoinObserverCommand>) this::handleJoinObserver);
        register(UserGameCommand.CommandType.MAKE_MOVE, (UserCommandHandler<MakeMoveCommand>) this::handleMakeMove);
        register(UserGameCommand.CommandType.LEAVE, (UserCommandHandler<LeaveCommand>) this::handleLeave);
        register(UserGameCommand.CommandType.RESIGN, (UserCommandHandler<ResignCommand>) this::handleResign);
        register(UserGameCommand.CommandType.VERBOSE, (UserCommandHandler<VerboseCommand>) this::handleVerbose);
    }

    /**
     * Used internally to receive string messages and send them to the respective handlers.
     */
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = GsonUtils.standard().fromJson(message, UserGameCommand.class);
            UserCommandHandler<?> handler = handlers[command.getCommandType().ordinal()];
            if (handler == null) return;
            handler.handleGeneric(command, session);
        } catch (Throwable exception) {
            sendError(session, "ERROR: " + exception.getMessage());
        }
    }

    /**
     * When a ws session closes, we will try to remove any trace of it from the GameManager and broadcast
     * their leaving to the game they are part of.
     */
    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        try {
            if (GameManager.sessionExists(user)) {
                SessionInfo info = GameManager.getInfo(user);
                GameManager.leaveGame(user);
                GameManager.broadcast(info.gameID(), new NotificationMessage(info.username() + " left the Game"));
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * On any sort of error remove session
     */
    @OnWebSocketError
    public void onError(Session session, Throwable ignored) {
        onClose(session, 0, null);
    }

    void handleJoinPlayer(JoinPlayerCommand command, Session session) throws Exception {
        AuthToken auth = WSUtils.validateAuthorization(command.getAuthString());

        GameManager.joinGame(command.gameID(), session, command.playerColor(), auth.username());
        sendLoadGame(session);
        GameManager.broadcast(command.gameID(), session, new NotificationMessage(auth.username() + " joined the game."));
    }

    void handleJoinObserver(JoinObserverCommand command, Session session) throws Exception {
        AuthToken auth = WSUtils.validateAuthorization(command.getAuthString());

        GameManager.joinGame(command.gameID(), session, null, auth.username());
        sendLoadGame(session);
        GameManager.broadcast(command.gameID(), session, new NotificationMessage(auth.username() + " is observing the Game"));
    }

    void handleMakeMove(MakeMoveCommand command, Session session) throws Exception {
        AuthToken auth = WSUtils.validateAuthorization(command.getAuthString());
        WSUtils.validateSession(session, command.gameID());

        ChessGame modifiedGame = GameManager.makeMove(session, command.move());
        Game game = GameManager.getInfo(session).game();
        GameManager.broadcast(command.gameID(), new LoadGameMessage(modifiedGame, game.state(), game.gameName()));
        GameManager.broadcast(command.gameID(), session, new NotificationMessage("Move made by " + auth.username() + ": " + command.move().toString()));

        ChessGame.TeamColor color = GameManager.getInfo(session).color().next();
        String otherUser = GameManager.getInfo(session).game().username(color);

        if (modifiedGame.isInCheckmate(color)) {
            GameManager.broadcast(command.gameID(), new NotificationMessage(otherUser + " is checkmated!"));
        } else if (modifiedGame.isInCheck(color)) {
            GameManager.broadcast(command.gameID(), new NotificationMessage(otherUser + " is in Check!"));
        } else if (modifiedGame.isInStalemate(color)) {
            GameManager.broadcast(command.gameID(), new NotificationMessage("Stalemate!"));
        }
    }

    void handleLeave(LeaveCommand command, Session session) throws Exception {
        AuthToken auth = WSUtils.validateAuthorization(command.getAuthString());
        WSUtils.validateSession(session, command.gameID());
        GameManager.leaveGame(session);
        GameManager.broadcast(command.gameID(), new NotificationMessage(auth.username() + " left the Game"));
    }

    void handleResign(ResignCommand command, Session session) throws SocketException, DataAccessException, IOException {
        AuthToken auth = WSUtils.validateAuthorization(command.getAuthString());
        WSUtils.validateSession(session, command.gameID());
        GameManager.resign(session);
        GameManager.broadcast(command.gameID(), new NotificationMessage(auth.username() + " has Resigned."));
        Game game = GameManager.getInfo(session).game();
        GameManager.broadcastVerbose(command.gameID(), new LoadGameMessage(game.game(), game.state(), game.gameName()));
    }

    void handleVerbose(VerboseCommand command, Session session) throws SocketException, DataAccessException {
        WSUtils.validateAuthorization(command.getAuthString());
        WSUtils.validateSession(session, command.gameID());
        GameManager.setVerbose(session);
    }

    /**
     * register a handler to a commandType
     */
    private void register(UserGameCommand.CommandType type, UserCommandHandler<?> handler) {
        handlers[type.ordinal()] = handler;
    }

    /**
     * Send an error message
     *
     * @param session Session to send error to
     * @param error   Error message to send
     */
    private void sendError(Session session, String error) throws IOException {
        session.getRemote().sendString(GsonUtils.standard().toJson(new ErrorMessage(error)));
    }

    /**
     * Send a Load game message
     */
    private void sendLoadGame(Session session) throws IOException, DataAccessException {
        Game game = GameManager.getInfo(session).game();
        LoadGameMessage message = new LoadGameMessage(game.game(), game.state(), game.gameName());
        session.getRemote().sendString(GsonUtils.standard().toJson(message));
    }

}