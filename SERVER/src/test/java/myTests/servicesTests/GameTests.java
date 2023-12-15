package myTests.servicesTests;

import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.database.dao.GameDAO;
import edu.blaylock.server.database.models.Game;
import edu.blaylock.server.exceptions.AlreadyTakenException;
import edu.blaylock.server.exceptions.BadRequestException;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.services.game.GameService;
import edu.blaylock.server.services.game.requests.CreateGameRequest;
import edu.blaylock.server.services.game.requests.JoinGameRequest;
import edu.blaylock.server.services.game.responses.CreateGameResponse;
import myTests.resources.DatabaseFactory;
import myTests.resources.RequestFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    @BeforeAll
    static void setupDatabase() throws Exception {
        DatabaseFactory.setupDatabase();
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException {
        ServerGlobals.database().dropDatabase();
    }

    @Test
    void testListGamesResponseWhenNoGamesCreated() throws DataAccessException {
        Game[] result = GameService.getGamesList().games();
        assertNotEquals(null, result, "response empty");
        assertEquals(0, result.length, "response contains games when it shouldn't");
    }

    @Test
    void testListGamesResponseMatchesGamesCreated() throws DataAccessException {
        Game[] actual = new Game[]{
                DatabaseFactory.createGame(1, "g1"),
                DatabaseFactory.createGame(2, "g2"),
                DatabaseFactory.createGame(3, "g3")
        };

        Game[] result = GameService.getGamesList().games();
        assertArrayEquals(actual, result, "Game list didn't return right values");
    }

    @Test
    void testFailCreateGameServiceWhenRequestNameEmpty() {
        Request emptyStringRequest = RequestFactory.newRequest("{\"gameName\":\"\"}");
        Request nullRequest = RequestFactory.newRequest("");

        assertThrows(BadRequestException.class, () -> CreateGameRequest.getRequest(emptyStringRequest),
                "Bad Request not thrown when gameName empty");

        assertThrows(BadRequestException.class, () -> CreateGameRequest.getRequest(nullRequest),
                "Bad Request not thrown when request body empty");

    }

    @Test
    void testCreateGameRespondsWithAppropriateID() throws DataAccessException {
        CreateGameRequest request1 = new CreateGameRequest("game1");
        CreateGameRequest request2 = new CreateGameRequest("game2");

        CreateGameResponse response1 = GameService.createGame(request1);
        CreateGameResponse response2 = GameService.createGame(request2);

        assertEquals(1, response1.gameID(), "IncorrectGameID");
        assertEquals(2, response2.gameID(), "Incorrect Game ID");

        assertEquals(2, DatabaseFactory.countGames(), "Incorrect number of games in database");

        GameDAO dao = new GameDAO(ServerGlobals.database());
        assertEquals("game1", dao.getGameById(response1.gameID()).gameName(), "Game Name doesn't Match");
        assertEquals("game2", dao.getGameById(response2.gameID()).gameName(), "Game NAme doesn't match");
    }

    @Test
    void testFailJoinGameWhenIdEmptyThrowsBadRequest() throws DataAccessException {
        DatabaseFactory.createAuthToken("token", "u1");
        DatabaseFactory.createGame(1, "Game1");

        Request emptyIDRequest = RequestFactory.newRequest("{\"playerColor\": \"WHITE\"}",
                "authorization:token");

        assertThrows(BadRequestException.class, () -> JoinGameRequest.getRequest(emptyIDRequest), "Exception doesn't match");

    }

    @Test
    void testFailJoinGameWhenColorInvalidThrowsBadRequest() throws DataAccessException, BadRequestException {
        DatabaseFactory.createAuthToken("token", "u1");
        DatabaseFactory.createGame(1, "Game1");
        Request sparkRequest = RequestFactory.newRequest("{\"playerColor\": \"invalidColor\", \"gameID\":1}",
                "authorization:token");

        JoinGameRequest gameRequest = JoinGameRequest.getRequest(sparkRequest);
        assertThrows(BadRequestException.class, () -> GameService.joinGame(gameRequest), "Exception doesn't match");
    }

    @Test
    void testJoinGameWhenObserver() throws DataAccessException, BadRequestException {
        DatabaseFactory.createAuthToken("token", "u1");
        DatabaseFactory.createGame(1, "Game1");
        Request sparkRequest = RequestFactory.newRequest("{\"playerColor\": \"\", \"gameID\":1}",
                "authorization:token");


        JoinGameRequest gameRequest = JoinGameRequest.getRequest(sparkRequest);

        assertDoesNotThrow(() -> GameService.joinGame(gameRequest));
    }

    @Test
    void testJoinGameWhenColorSpecified() throws DataAccessException, BadRequestException, AlreadyTakenException {
        DatabaseFactory.createAuthToken("token", "u1");
        DatabaseFactory.createGame(1, "Game1");
        Request whiteRequest = RequestFactory.newRequest("{\"playerColor\": \"WHITE\", \"gameID\":1}",
                "authorization:token");

        Request blackRequest = RequestFactory.newRequest("{\"playerColor\": \"BLACK\", \"gameID\":1}",
                "authorization:token");

        JoinGameRequest gameWhiteRequest = JoinGameRequest.getRequest(whiteRequest);
        JoinGameRequest gameBlackRequest = JoinGameRequest.getRequest(blackRequest);

        GameService.joinGame(gameWhiteRequest);
        assertEquals("u1", new GameDAO(ServerGlobals.database()).getGameById(1).whiteUsername(), "Username not in db");

        GameService.joinGame(gameBlackRequest);
        assertEquals("u1", new GameDAO(ServerGlobals.database()).getGameById(1).blackUsername(), "Username not in db");
    }

    @Test
    void testFailJoinGameWhenNameTakenThrowsAlreadyTaken() throws DataAccessException, BadRequestException, AlreadyTakenException {
        DatabaseFactory.createAuthToken("token", "u1");
        DatabaseFactory.createGame(1, "Game1");
        Request sparkRequest = RequestFactory.newRequest("{\"playerColor\": \"WHITE\", \"gameID\":1}",
                "authorization:token");

        JoinGameRequest gameRequest = JoinGameRequest.getRequest(sparkRequest);
        GameService.joinGame(gameRequest);
        assertThrows(AlreadyTakenException.class, () -> GameService.joinGame(gameRequest), "Exception doesn't match");
    }

}
