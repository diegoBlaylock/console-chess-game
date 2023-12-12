package edu.blaylock.server.gameplay;

import chess.ChessGame;
import edu.blaylock.server.database.dao.GameDAO;
import edu.blaylock.server.database.models.Game;
import edu.blaylock.server.exceptions.DataAccessException;

/**
 * Contains basic info about a session
 *
 * @param gameID   What game is it participating in
 * @param color    What color is it part of
 * @param username What is the username of the fellow
 * @param verbose  Whether is should receive verbose messages
 */
public record SessionInfo(int gameID, ChessGame.TeamColor color, String username, boolean verbose) {
    public SessionInfo(int gameID, ChessGame.TeamColor color, String username) {
        this(gameID, color, username, false);
    }

    /**
     * @return GameModel associated with this session
     * @throws DataAccessException Error with database
     */
    public Game game() throws DataAccessException {
        return new GameDAO().getGameById(gameID);
    }

    /**
     * @return ChessGame associated with this session. Makes the code a little bit more readable
     * @throws DataAccessException Error with database
     */
    public ChessGame chessGame() throws DataAccessException {
        return game().game();
    }

    /**
     * return a copy of this instance with the verbose flag set
     *
     * @return copy
     */
    public SessionInfo makeVerbose() {
        return new SessionInfo(gameID, color, username, true);
    }
}
