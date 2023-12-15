package edu.blaylock.server.gameplay;

import chess.ChessGame;
import chess.ChessMove;
import edu.blaylock.chess.GameState;
import edu.blaylock.server.database.dao.GameDAO;
import edu.blaylock.server.database.models.Game;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.exceptions.SocketException;
import edu.blaylock.utils.gson.GsonUtils;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {

    /**
     * Maps game ID to Game info
     */
    private static final Map<Integer, GameInfo> gameToInfo = new ConcurrentHashMap<>();

    /**
     * Maps Session to Session Info
     */
    private static final Map<Session, SessionInfo> sessionToInfo = new ConcurrentHashMap<>();

    /**
     * Used when the database is cleared. Clears all stored data from GameManager.
     */
    public static void clear() {
        gameToInfo.clear();
        sessionToInfo.clear();
    }

    /**
     * Join a session to a game, Checks if the color and username matches those in the database.
     *
     * @param gameID   Game ID to join
     * @param session  session to join
     * @param color    color to join to
     * @param username username to verify
     * @throws SocketException     If there is a problem with verification
     * @throws DataAccessException Error with the database
     */
    public static void joinGame(int gameID, Session session, ChessGame.TeamColor color, String username) throws SocketException, DataAccessException {
        Game game = WSUtils.validateGame(gameID);
        if (color != null) WSUtils.validateColorMatch(username, color, game, WSUtils.UNAUTHORIZED);
        GameInfo gameInfo = gameToInfo.computeIfAbsent(gameID, ignored -> new GameInfo());
        gameInfo.joinGame(color, session);
        sessionToInfo.put(session, new SessionInfo(gameID, color, username));
    }

    /**
     * Removes all data about a session within this manager as well as within a game from the database.
     *
     * @param session Session to remove
     * @throws SocketException     Person is already removed
     * @throws DataAccessException Error with database
     */
    public static void leaveGame(Session session) throws SocketException, DataAccessException {
        SessionInfo sessionInfo = sessionToInfo.get(session);
        GameInfo gameInfo = gameToInfo.get(sessionInfo.gameID());
        gameInfo.remove(session, sessionInfo.color());
        synchronized (gameInfo.gameMutex()) {
            if (sessionInfo.color() != null)
                new GameDAO().removeGamePlayer(sessionInfo.gameID(), sessionInfo.username(), sessionInfo.color());
        }
        sessionToInfo.remove(session);
        if (gameInfo.empty()) {
            gameToInfo.remove(sessionInfo.gameID());
        }
    }

    /**
     * Pulls the chessgame from database and makes a move. Makes sure the game is in correct state and that the proper
     * client is doing this.
     *
     * @param session Session who asked
     * @param move    Move to make
     * @return modified ChessGame
     * @throws Exception Invalid Move, Unauthorized, Game finished
     */
    public static ChessGame makeMove(Session session, ChessMove move) throws Exception {
        SessionInfo info = getInfo(session);
        if (info.game().state() != GameState.UNFINISHED) throw new SocketException(WSUtils.GAME_ENDED);
        WSUtils.validateColorMatch(info.username(), info.chessGame().getTeamTurn(), info.game(), WSUtils.OUT_OF_TURN);
        if (GameManager.getInfo(session).color() != info.chessGame().getTeamTurn())
            throw new SocketException(WSUtils.OUT_OF_TURN);

        GameInfo sessions = gameToInfo.get(info.gameID());

        synchronized (sessions.gameMutex()) {
            ChessGame game = info.chessGame();
            game.makeMove(move);
            new GameDAO().updateChessGame(info.gameID(), game);

            if (game.isInCheckmate(game.getTeamTurn())) {
                new GameDAO().setGameState(info.gameID(), GameState.CHECKMATE);
            } else if (game.isInStalemate(game.getTeamTurn())) {
                new GameDAO().setGameState(info.gameID(), GameState.STALEMATE);
            }

            return game;
        }
    }

    /**
     * If done by the proper session, the game will be put into a resigned state.
     *
     * @param session Session requesting resign
     * @throws DataAccessException Error with database
     * @throws SocketException     Already resigned or not permitted i.e. observer
     */
    public static void resign(Session session) throws DataAccessException, SocketException {
        SessionInfo info = getInfo(session);
        if (info.color() == null) throw new SocketException("Can't Resign as Observer");
        if (info.game().state() != GameState.UNFINISHED) throw new SocketException(WSUtils.GAME_ENDED);
        new GameDAO().setGameState(info.gameID(), GameState.RESIGNED);

    }

    /**
     * For a session, sets the verbose flag, meaning that this client will receive more info (needed for the ui)
     *
     * @param session Session to set as verbose
     */
    public static void setVerbose(Session session) {
        SessionInfo info = getInfo(session);
        sessionToInfo.put(session, info.makeVerbose());
    }

    /**
     * Broadcast a ServerMessage to all session involved within a game. Will exclude the originator from the broadcast
     * if included.
     *
     * @param gameID     Game to which to broadcast
     * @param originator Session to exclude from broadcast. If null, all will receive the message
     * @param message    Message to send
     * @throws SocketException Game doesn't exist
     * @throws IOException     Error sending message
     */
    public static void broadcast(int gameID, Session originator, ServerMessage message) throws SocketException, IOException {
        GameInfo sessions = gameToInfo.getOrDefault(gameID, null);
        if (sessions == null) throw new SocketException(WSUtils.BAD_GAME_ID);

        String string = GsonUtils.standard().toJson(message);

        synchronized (sessions.sessionMutex()) {
            for (Session player : sessions.players())
                if ((originator == null || originator != player) && player != null)
                    player.getRemote().sendString(string);

            for (Session observer : sessions.observers())
                if ((originator == null || originator != observer) && observer != null)
                    observer.getRemote().sendString(string);
        }
    }

    /**
     * Send broadcast to everyone
     *
     * @param gameID  game to which to broadcast
     * @param message Message to broadcast
     * @throws SocketException Game doesn't exist
     * @throws IOException     Error sending message
     */
    public static void broadcast(int gameID, ServerMessage message) throws SocketException, IOException {
        broadcast(gameID, null, message);
    }

    /**
     * Will only broadcast to those clients with the verbose flag
     *
     * @param gameID  Game to broadcast to
     * @param message Message to broadcast
     * @throws SocketException Game doesn't exist
     * @throws IOException     Error sending broadcast
     */
    public static void broadcastVerbose(int gameID, ServerMessage message) throws SocketException, IOException {
        GameInfo sessions = gameToInfo.getOrDefault(gameID, null);
        if (sessions == null) throw new SocketException(WSUtils.BAD_GAME_ID);

        String string = GsonUtils.standard().toJson(message);

        synchronized (sessions.sessionMutex()) {
            for (Session player : sessions.players())
                if (player != null && getInfo(player).verbose())
                    player.getRemote().sendString(string);

            for (Session observer : sessions.observers())
                if (observer != null && getInfo(observer).verbose())
                    observer.getRemote().sendString(string);
        }
    }

    /**
     * @param session Session to check
     * @return whether the session is in a game
     */
    public static boolean sessionExists(Session session) {
        return sessionToInfo.containsKey(session);
    }

    /**
     * @param session Session to check
     * @return The Info associated with a session. Null if nothing exists
     */
    public static SessionInfo getInfo(Session session) {
        if (session == null) return null;
        return sessionToInfo.getOrDefault(session, null);
    }

}
