package myTests.resources;

import chess.ChessGame;
import edu.blaylock.chess.GameState;
import edu.blaylock.chess.impl.ChessBoardFactory;
import edu.blaylock.chess.impl.ChessGameImpl;
import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.database.dao.AuthTokenDAO;
import edu.blaylock.server.database.dao.GameDAO;
import edu.blaylock.server.database.dao.UserDAO;
import edu.blaylock.server.database.implementations.mysql.MySqlDatabase;
import edu.blaylock.server.database.models.AuthToken;
import edu.blaylock.server.database.models.Game;
import edu.blaylock.server.database.models.User;
import edu.blaylock.server.database.tablespecs.AuthTokenSpec;
import edu.blaylock.server.database.tablespecs.GameSpec;
import edu.blaylock.server.database.tablespecs.UserSpec;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.services.user.UserUtils;

public class DatabaseFactory {

    public static void setupDatabase() throws Exception {
        ServerGlobals.database(new MySqlDatabase());

        ServerGlobals.database().createTable(UserSpec.class);
        ServerGlobals.database().createTable(AuthTokenSpec.class);
        ServerGlobals.database().createTable(GameSpec.class);
    }

    public static User createUser(String username, String password, String email) throws DataAccessException {
        UserDAO dao = new UserDAO(ServerGlobals.database());
        User result = new User(username, UserUtils.saltedHash(password), email);
        dao.create(result);
        return result;
    }

    public static Game createGame(int id, String name, ChessGame game) throws DataAccessException {
        GameDAO dao = new GameDAO(ServerGlobals.database());
        Game store = new Game(id, null, null, name, game, GameState.UNFINISHED);
        dao.create(store);
        return store;
    }

    public static Game createGame(int id, String name) throws DataAccessException {
        ChessGame game = new ChessGameImpl();
        game.setBoard(ChessBoardFactory.defaultChessBoard());
        return createGame(id, name, game);
    }

    public static AuthToken createAuthToken(String token, String username) throws DataAccessException {
        AuthTokenDAO dao = new AuthTokenDAO(ServerGlobals.database());
        AuthToken result = new AuthToken(token, username);
        dao.create(result);
        return result;
    }


    public static int countUsers() throws DataAccessException {
        UserDAO dao = new UserDAO(ServerGlobals.database());
        return dao.findAll().length;
    }

    public static int countGames() throws DataAccessException {
        GameDAO dao = new GameDAO(ServerGlobals.database());
        return dao.findAll().length;
    }

    public static int countAuthTokens() throws DataAccessException {
        AuthTokenDAO dao = new AuthTokenDAO(ServerGlobals.database());
        return dao.findAll().length;
    }


}
