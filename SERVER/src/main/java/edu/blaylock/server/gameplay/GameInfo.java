package edu.blaylock.server.gameplay;

import chess.ChessGame;
import edu.blaylock.server.exceptions.SocketException;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains various pieces of information about a game referenced to by the gameID. An array of player sessions (based
 * TeamColor enum ordinal) as well as a collection of observers. Two mutexes are available to lock the chess game as
 * well as the structures holding the sessions.
 */
class GameInfo {
    private final Object gameMutex = new Object();
    private final Object sessionMutex = new Object();
    private final Session[] players = new Session[2];
    private final Set<Session> observers = ConcurrentHashMap.newKeySet();

    public Session[] players() {
        return players;
    }

    public Collection<Session> observers() {
        return observers;
    }

    public Object gameMutex() {
        return gameMutex;
    }

    public Object sessionMutex() {
        return sessionMutex;
    }

    /**
     * Will add a session to this class depending on color
     *
     * @param color   Color to add
     * @param session Session to add
     */
    public void joinGame(ChessGame.TeamColor color, Session session) {
        synchronized (sessionMutex) {
            if (color == null) observers.add(session);
            else {
                players[color.ordinal()] = session;
            }
        }
    }

    /**
     * Removes a session by color
     *
     * @param session session to remove
     * @param color   color to remove
     * @throws SocketException Not part of the game
     */
    public void remove(Session session, ChessGame.TeamColor color) throws SocketException {
        synchronized (sessionMutex) {
            if (color == null) {
                if (!observers.contains(session)) throw new SocketException("Your haven't joined the game!");

                observers.remove(session);
            } else {
                if (!session.equals(players[color.ordinal()]))
                    throw new SocketException("Your not part of the game!");

                players[color.ordinal()] = null;
            }

        }
    }

    /**
     * Whether game is empty, used to know whether to remove locally
     *
     * @return Whether any sessions are present in game
     */
    public boolean empty() {
        synchronized (sessionMutex) {
            return observers.isEmpty() && players[0] == null && players[1] == null;
        }
    }
}
