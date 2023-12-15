package edu.blaylock.server.database.dao;

import edu.blaylock.server.database.implementations.IDatabase;
import chess.ChessGame;
import edu.blaylock.chess.GameState;
import edu.blaylock.server.database.models.Game;
import edu.blaylock.server.database.tablespecs.GameSpec;
import edu.blaylock.server.database.tablespecs.TableSpec;
import edu.blaylock.server.exceptions.AlreadyTakenException;
import edu.blaylock.server.exceptions.DataAccessException;

import java.math.BigInteger;

/**
 * DAO object describing the following table:<br/>
 * Name - "blaylock_game"<br/>
 * Fields (Name:DBType:JavaType) -<br/>
 * &emsp;("gameID":Integer:Integer), ("whiteUsername":String:String), ("blackUsername":String:String),
 * ("gameName":String:String), ("game":String:ChessGame)<br/>
 * Conversion - Game to/from Object[5]<br><br>
 * <p>
 * You may be wondering where in this, the ChessGame is serialized, the answer is that all that conversion is handles by
 * the GameSpec, which contains an array of Fields with methods to serialize and deserialize. MySqlDatabase will handle
 * all sql code and rely on the TableSpecs to convert models into array of sql objects,
 * allowing for an extensible way to easily include more models.
 */
public class GameDAO extends DAO<Game> {
    /**
     * Table description
     */
    public static final TableSpec<Game> SPECIFICATION = new GameSpec();

    /**
     * Create new DAO for Game with given Database/Connection
     *
     * @param database IDatabase to call
     */
    public GameDAO(IDatabase database) throws DataAccessException {
        super(database);
    }

    public GameDAO() throws DataAccessException {
        super();
    }

    public int createGame(Game game) throws DataAccessException {
        return ((BigInteger) this.database.addRecordGetGeneratedKeys(getSpecification(), game)).intValue();
    }

    /**
     * Get Game model from gameID
     *
     * @param id gameID
     * @return Game if found, null otherwise
     * @throws DataAccessException Database error
     */
    public Game getGameById(int id) throws DataAccessException {
        Game[] models = findModelsByAttribute("gameID", id);
        if (models.length == 0) return null;
        return models[0];
    }

    /**
     * Populates game with username for appropriate team color
     *
     * @param gameId   gameID
     * @param username name of user
     * @param color    color
     * @throws DataAccessException   Database error
     * @throws AlreadyTakenException The specified game has a user for that color
     */
    public void setGamePlayer(int gameId, String username, ChessGame.TeamColor color)
            throws DataAccessException, AlreadyTakenException {
        Game currentGame = getGameById(gameId);
        if (currentGame == null) throw new DataAccessException("Game id doesn't exist");

        if (currentGame.username(color) != null)
            throw new AlreadyTakenException();

        this.updateFieldByFieldAttribute("gameID", gameId, getUsernameFieldByColor(color), username);
    }

    /**
     * Sets a game's player to null if the game contains the player with username and color
     *
     * @param gameId   gameID
     * @param username name of user
     * @param color    color
     * @throws DataAccessException Database error
     */
    public void removeGamePlayer(int gameId, String username, ChessGame.TeamColor color)
            throws DataAccessException {
        Game currentGame = getGameById(gameId);
        if (currentGame == null) throw new DataAccessException("Game id doesn't exist");

        if (!username.equals(currentGame.username(color))) return;

        this.updateFieldByFieldAttribute("gameID", gameId, getUsernameFieldByColor(color), null);
    }

    /**
     * Update database to hold correct state for game
     *
     * @param gameID gameID to update
     * @param state  state to change
     * @throws DataAccessException database error
     */
    public void setGameState(int gameID, GameState state) throws DataAccessException {
        if (getGameById(gameID) == null) throw new DataAccessException("Game id doesn't exist");
        this.updateFieldByFieldAttribute("gameID", gameID, "state", state);
    }


    /**
     * Update database to hold modified chessGame
     *
     * @param gameId    gameID to update
     * @param chessGame modified game
     * @throws DataAccessException database error
     */
    public void updateChessGame(int gameId, ChessGame chessGame) throws DataAccessException {
        int num_updated = this.updateFieldByFieldAttribute("gameID", gameId, "game", chessGame);
        if (num_updated < 1) throw new DataAccessException("Couldn't find gameid to update");
    }

    private String getUsernameFieldByColor(ChessGame.TeamColor color) {
        return switch (color) {
            case BLACK -> "blackUsername";
            case WHITE -> "whiteUsername";
        };
    }

    @Override
    public TableSpec<Game> getSpecification() {
        return SPECIFICATION;
    }
}
