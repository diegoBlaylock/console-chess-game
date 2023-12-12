package edu.blaylock.server.gameplay;

import chess.ChessGame;
import edu.blaylock.server.database.dao.AuthTokenDAO;
import edu.blaylock.server.database.dao.GameDAO;
import edu.blaylock.server.database.models.AuthToken;
import edu.blaylock.server.database.models.Game;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.exceptions.SocketException;
import org.eclipse.jetty.websocket.api.Session;

/**
 * Utility class
 */
public class WSUtils {
    /// ERROR MESSAGES ///
    public final static String UNAUTHORIZED = "Not Authorized!";
    public final static String BAD_GAME_ID = "Game ID not recognized!";
    public final static String OUT_OF_TURN = "Not your turn to Move!";
    public final static String GAME_ENDED = "Game has already ended!";

    /**
     * Makes sure a game exists in the database
     *
     * @param gameID game id
     * @return Game if it exists
     * @throws SocketException     if the game doesn't exist
     * @throws DataAccessException if there was a database error
     */
    public static Game validateGame(int gameID) throws SocketException, DataAccessException {
        Game game = new GameDAO().getGameById(gameID);
        if (game == null) throw new SocketException(BAD_GAME_ID);
        return game;
    }

    /**
     * Makes sure that the authToken exists
     *
     * @param authToken String token to check
     * @return AuthToken model
     * @throws SocketException     Doesn't exist
     * @throws DataAccessException Database error
     */
    public static AuthToken validateAuthorization(String authToken) throws SocketException, DataAccessException {
        AuthToken auth = new AuthTokenDAO().getAuthToken(authToken);
        if (auth == null) throw new SocketException(UNAUTHORIZED);
        return auth;
    }

    /**
     * Makes sure that for a game model, the username and color match that stored in game
     *
     * @param username     username to check
     * @param color        color to check
     * @param game         Data to check against
     * @param errorMessage Error message to throw
     * @throws SocketException Thrown if doesn't match
     */
    public static void validateColorMatch(String username, ChessGame.TeamColor color, Game game, String errorMessage) throws SocketException {
        if (!username.equals(game.username(color))) throw new SocketException(errorMessage);
    }

    /**
     * Makes sure that a session exists and matches the game id given
     *
     * @param session session to check
     * @param gameID  id to check
     * @throws SocketException Thrown if doesn't match
     */
    public static void validateSession(Session session, int gameID) throws SocketException {
        if (!GameManager.sessionExists(session) || GameManager.getInfo(session).gameID() != gameID)
            throw new SocketException(UNAUTHORIZED);
    }

}
