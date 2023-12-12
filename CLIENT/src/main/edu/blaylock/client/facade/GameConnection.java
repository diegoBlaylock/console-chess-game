package edu.blaylock.client.facade;

import chess.ChessGame;
import chess.ChessMove;
import edu.blaylock.client.facade.exceptions.ConnectionException;
import edu.blaylock.client.facade.handlers.ServerMessageHandler;
import edu.blaylock.utils.gson.GsonUtils;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

/**
 * Contains the Websocket session as well as authorization and gameID. Has methods to send UserGameCommands through
 * the session.
 */
public class GameConnection extends Endpoint {
    private final ServerMessageHandler<?>[] handlers = new ServerMessageHandler[ServerMessage.ServerMessageType.values().length];
    private final Session session;
    private final String authToken;
    private final int gameID;

    /**
     * Open a new connection the server specified by the url. Hold state for the token and gameid
     *
     * @param wsAddress url to connect to
     * @param authToken token for authorization
     * @param gameID    game to connect to
     * @throws ConnectionException Couldn't connect
     */
    public GameConnection(String wsAddress, String authToken, int gameID) throws ConnectionException {
        this.authToken = authToken;
        this.gameID = gameID;

        try {
            URI uri = new URI(wsAddress);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);
            this.session.addMessageHandler(String.class, this::onMessage);
        } catch (Exception e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    /**
     * Register a commandType to a handler for when the server sends a message. Will only hold one handler per type
     * at a single time.
     *
     * @param type    Type to handle
     * @param handler Handler to use
     * @param <T>     ServerMessage type
     */
    public <T extends ServerMessage> void register(ServerMessage.ServerMessageType type, ServerMessageHandler<T> handler) {
        synchronized (handlers) {
            handlers[type.ordinal()] = handler;
        }
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/6-gameplay/gameplay.md">
     * Spec
     * </a>
     */
    public void joinGame(ChessGame.TeamColor color) throws IOException {
        if (color == null) sendCommand(new JoinObserverCommand(authToken, gameID));
        else sendCommand(new JoinPlayerCommand(authToken, gameID, color));
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/6-gameplay/gameplay.md">
     * Spec
     * </a>
     */
    public void makeMove(ChessMove move) throws IOException {
        sendCommand(new MakeMoveCommand(authToken, gameID, move));
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/6-gameplay/gameplay.md">
     * Spec
     * </a>
     */
    public void leave() throws IOException {
        sendCommand(new LeaveCommand(authToken, gameID));
        close();
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/6-gameplay/gameplay.md">
     * Spec
     * </a>
     */
    public void resign() throws IOException {
        sendCommand(new ResignCommand(authToken, gameID));
    }

    /**
     * Sets the verbose flag meaning the server will send messages necessary for the UI. This way I can customise
     * the server behaviour and still pass the websocketTests
     *
     * @throws IOException Error communicating
     */
    public void verbose() throws IOException {
        sendCommand(new VerboseCommand(authToken, gameID));
    }

    /**
     * Closes the websocket
     *
     * @throws IOException Error closing the socket
     */
    public void close() throws IOException {
        session.close();
    }

    /**
     * Dump a UserCommand to json and send through websocket
     *
     * @param command command to send
     * @throws IOException Error with websocket
     */
    private void sendCommand(UserGameCommand command) throws IOException {
        session.getBasicRemote().sendText(GsonUtils.standard().toJson(command));
    }

    /**
     * Will receive a message from server and direct it towards the appropriate handler
     */
    private void onMessage(String string) {
        ServerMessage message = GsonUtils.standard().fromJson(string, ServerMessage.class);

        ServerMessageHandler<?> handler;
        synchronized (handlers) {
            handler = handlers[message.getServerMessageType().ordinal()];
        }
        if (handler == null) return;
        handler.handleGeneric(message);
    }

    /**
     * Not used for anything, but required by Endpoint interface
     */
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
