package myTests.servicesTests;

import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.handlers.HandlerUtils;
import myTests.resources.DatabaseFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearTest {
    @BeforeAll
    static void setupGeneral() throws Exception {
        //Populate various tables
        DatabaseFactory.setupDatabase();
        ServerGlobals.database().dropDatabase();

        DatabaseFactory.createUser("u1", "p1", "none1");
        DatabaseFactory.createUser("u2", "p1", "none2");

        DatabaseFactory.createGame(1, "g1");
        DatabaseFactory.createGame(2, "g2");

        DatabaseFactory.createAuthToken("1", "u1");
        DatabaseFactory.createAuthToken("2", "u2");
    }

    @Test
    void testDatabasePopulated() throws DataAccessException {
        assertEquals(2, DatabaseFactory.countUsers(),
                "Number of user in database doesn't match number created");
        assertEquals(2, DatabaseFactory.countGames(),
                "Number of games in database doesn't match number created");
        assertEquals(2, DatabaseFactory.countAuthTokens(),
                "Number of tokens in database doesn't match number created");
    }

    @Test
    void testDatabaseClear() throws DataAccessException {
        HandlerUtils.dropDatabase();

        assertEquals(0, DatabaseFactory.countUsers(),
                "User table wasn't cleared");
        assertEquals(0, DatabaseFactory.countGames(),
                "Game table wasn't cleared");
        assertEquals(0, DatabaseFactory.countAuthTokens(),
                "Authtoken table wasn't cleared");

    }

}
