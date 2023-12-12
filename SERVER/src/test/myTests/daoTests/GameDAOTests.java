package myTests.daoTests;

import chess.ChessGame;
import chess.InvalidMoveException;
import edu.blaylock.chess.impl.ChessBoardFactory;
import edu.blaylock.chess.impl.ChessGameImpl;
import edu.blaylock.chess.impl.ChessMoveImpl;
import edu.blaylock.chess.impl.ChessPositionImpl;
import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.database.dao.GameDAO;
import edu.blaylock.server.database.models.Game;
import edu.blaylock.server.exceptions.AlreadyTakenException;
import edu.blaylock.server.exceptions.DataAccessException;
import myTests.resources.DatabaseFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameDAOTests {
    @BeforeAll
    static void setupDatabase() throws Exception {
        DatabaseFactory.setupDatabase();
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException {
        ServerGlobals.database().dropDatabase();
    }

    @Test
    void testGetGameByIdSucceeds() throws DataAccessException {
        Game game1 = DatabaseFactory.createGame(1, "game1");
        Game game2 = DatabaseFactory.createGame(2, "game2");

        GameDAO dao = new GameDAO();

        Assertions.assertEquals(game1, dao.getGameById(1));
        Assertions.assertEquals(game2, dao.getGameById(2));
    }

    @Test
    void testFailGetGameByIdBadId() throws DataAccessException {
        GameDAO dao = new GameDAO();

        Assertions.assertNull(dao.getGameById(1));
        Assertions.assertNull(dao.getGameById(-1));
    }

    @Test
    void testFindAllReturnsAll() throws DataAccessException {
        Game[] games = new Game[]{
                DatabaseFactory.createGame(1, "game1"),
                DatabaseFactory.createGame(2, "game2")
        };

        GameDAO dao = new GameDAO();

        Assertions.assertArrayEquals(games, dao.findAll());
    }

    @Test
    void testFindAllReturnsEmpty() throws DataAccessException {
        GameDAO dao = new GameDAO();

        Assertions.assertEquals(0, dao.findAll().length);
    }

    @Test
    void testUpdateGameSucceeds() throws DataAccessException, InvalidMoveException {
        DatabaseFactory.createGame(1, "game1", null);
        ChessGame game = new ChessGameImpl();
        game.setBoard(ChessBoardFactory.defaultChessBoard().copy());

        GameDAO dao = new GameDAO();
        Assertions.assertNull(dao.getGameById(1).game());

        dao.updateChessGame(1, game);
        Assertions.assertEquals(game, dao.getGameById(1).game());
        game.makeMove(new ChessMoveImpl(new ChessPositionImpl(2, 1), new ChessPositionImpl(4, 1), null));

        dao.updateChessGame(1, game);
        Assertions.assertEquals(game, dao.getGameById(1).game());
    }

    @Test
    void testUpdateGameFailsWhenNonExistent() throws DataAccessException {
        ChessGame game = new ChessGameImpl();
        game.setBoard(ChessBoardFactory.defaultChessBoard().copy());

        GameDAO dao = new GameDAO();

        Assertions.assertThrows(DataAccessException.class, () -> dao.updateChessGame(1, game));
    }

    @Test
    void testSetGamePlayerSucceeds() throws DataAccessException, AlreadyTakenException {
        DatabaseFactory.createGame(1, "game1", null);

        GameDAO dao = new GameDAO();
        Assertions.assertNull(dao.getGameById(1).whiteUsername());
        dao.setGamePlayer(1, "u1", ChessGame.TeamColor.WHITE);
        Assertions.assertEquals("u1", dao.getGameById(1).whiteUsername());
        dao.setGamePlayer(1, "u2", ChessGame.TeamColor.BLACK);
        Assertions.assertEquals("u2", dao.getGameById(1).blackUsername());
    }

    @Test
    void testSetGamePlayerFailsWhenNonExistent() throws DataAccessException, AlreadyTakenException {
        GameDAO dao = new GameDAO();
        Assertions.assertThrows(DataAccessException.class, () -> dao.setGamePlayer(1, "u1", ChessGame.TeamColor.WHITE));
        DatabaseFactory.createGame(1, "game1", null);

        dao.setGamePlayer(1, "u1", ChessGame.TeamColor.WHITE);
        Assertions.assertThrows(AlreadyTakenException.class, () -> dao.setGamePlayer(1, "u1", ChessGame.TeamColor.WHITE));
    }

    /*
    set game player
    update chessgame
    list all games
     */
}
