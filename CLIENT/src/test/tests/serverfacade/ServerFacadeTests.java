package tests.serverfacade;

import chess.ChessGame;
import edu.blaylock.client.Main;
import edu.blaylock.client.facade.GameConnection;
import edu.blaylock.client.facade.ServerFacade;
import edu.blaylock.client.facade.exceptions.ConnectionException;
import edu.blaylock.client.facade.exceptions.ServerException;
import edu.blaylock.client.facade.responses.ListGamesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class ServerFacadeTests {
    static ServerFacade facade = Main.SERVER;


    @BeforeEach
    void resetServer() throws ServerException, ConnectionException {
        facade.clearDB();
    }

    @Test
    void testRegisterUserSuccessful() {
        Assertions.assertDoesNotThrow(() -> facade.registerUser("u1", "p1", "e1"));
    }

    @Test
    void testRegisterUserFailsEmptyInput() {
        Assertions.assertThrows(ServerException.class, () -> facade.registerUser("", "", ""));
    }

    @Test
    void testLoginSuccessful() throws ServerException, ConnectionException {

        facade.registerUser("u1", "p1", "e1");
        Assertions.assertDoesNotThrow(() -> facade.login("u1", "p1"));
    }

    @Test
    public void testLoginBadPasswordOrUsername() {
        Assertions.assertThrows(ServerException.class, () -> facade.login("bad_username", "bad_password"));
    }

    @Test
    public void testLogoutSuccessful() throws ServerException, ConnectionException {
        facade.registerUser("u1", "p1", "e1");
        Assertions.assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    public void testLogoutFailsNoLogin() {
        Assertions.assertThrows(ServerException.class, () -> facade.logout());
    }

    @Test
    public void testListGamesSuccessful() throws ServerException, ConnectionException {
        facade.registerUser("u1", "p1", "e1");
        Assertions.assertDoesNotThrow(() -> facade.listGames());
    }

    @Test
    public void testListGamesFailsNoLogin() {
        Assertions.assertThrows(ServerException.class, () -> facade.listGames());
    }

    @Test
    public void testCreateGameSuccessful() throws ServerException, ConnectionException {
        facade.registerUser("u1", "p1", "e1");
        Assertions.assertDoesNotThrow(() -> facade.createGame("Game Name"));
        ListGamesResponse.GameDescription[] games = facade.listGames().games();
        Assertions.assertEquals(1, games.length);
        Assertions.assertEquals("Game Name", games[0].gameName());
    }

    @Test
    public void testCreateGameFailsNoInput() throws ServerException, ConnectionException {
        facade.registerUser("u1", "p1", "e1");
        Assertions.assertThrows(ServerException.class, () -> facade.createGame(""));
    }

    @Test
    public void testJoinGameSuccessful() throws ServerException, ConnectionException {
        facade.registerUser("u1", "p1", "e1");
        facade.createGame("Game Name");
        Assertions.assertDoesNotThrow(() -> facade.joinGame("WHITE", 1));
        ListGamesResponse.GameDescription[] games = facade.listGames().games();
        Assertions.assertEquals("u1", games[0].whiteUsername());
    }

    @Test
    public void testJoinGameFailsBadGameIDBadColor() throws ServerException, ConnectionException {
        facade.registerUser("u1", "p1", "e1");
        facade.createGame("Game Name");
        Assertions.assertThrows(ServerException.class, () -> facade.joinGame("WHITE", 24601));
        Assertions.assertThrows(ServerException.class, () -> facade.joinGame("BAD_COLOR", 1));

    }

    @Test
    public void testConnect() throws ServerException, ConnectionException, URISyntaxException, IOException, InterruptedException {
        ServerFacade alice = new ServerFacade(Main.SERVER_ADDRESS);
        ServerFacade bob = new ServerFacade(Main.SERVER_ADDRESS);
        alice.registerUser("alice", "p1", "alice@gmail.com");
        bob.registerUser("bob", "p1", "bob@gmail.com");
        alice.createGame("Game Name");
        alice.joinGame("WHITE", 1);
        bob.joinGame("BLACK", 1);
    
        GameConnection bConnection = bob.connect(1);
        GameConnection aConnection = alice.connect(1);

        Utils.Buffer aBuffer = new Utils.Buffer(aConnection);
        Utils.Buffer bBuffer = new Utils.Buffer(bConnection);
        bConnection.joinGame(ChessGame.TeamColor.BLACK);
        aConnection.joinGame(ChessGame.TeamColor.WHITE);
        Thread.sleep(50);
        System.out.println(bBuffer.getMessage());
    }

}
