package myTests.daoTests;

import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.database.dao.AuthTokenDAO;
import edu.blaylock.server.database.models.AuthToken;
import edu.blaylock.server.exceptions.DataAccessException;
import myTests.resources.DatabaseFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthTokenDAOTests {

    /*
    create,
    replace,

     */
    @BeforeAll
    static void setupDatabase() throws Exception {
        DatabaseFactory.setupDatabase();
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException {
        ServerGlobals.database().dropDatabase();
    }

    @Test
    void testUpdateOrCreateAuthToken() throws DataAccessException {
        AuthTokenDAO dao = new AuthTokenDAO();
        Assertions.assertDoesNotThrow(() -> dao.updateOrCreate(new AuthToken("token1", "u1")));
        Assertions.assertEquals(1, DatabaseFactory.countAuthTokens());
        Assertions.assertDoesNotThrow(() -> dao.updateOrCreate(new AuthToken("token2", "u2")));
        Assertions.assertEquals(2, DatabaseFactory.countAuthTokens());
        Assertions.assertDoesNotThrow(() -> dao.updateOrCreate(new AuthToken("new_token1", "u1")));
        Assertions.assertEquals(2, DatabaseFactory.countAuthTokens());
    }

    @Test
    void testFailUpdateOrCreateAuthTokenWhenUsernameOrTokenNull() throws DataAccessException {
        AuthTokenDAO dao = new AuthTokenDAO();
        Assertions.assertDoesNotThrow(() -> dao.updateOrCreate(new AuthToken("u1", "token1")));
        Assertions.assertThrows(DataAccessException.class, () -> dao.updateOrCreate(new AuthToken(null, "token2")));
        Assertions.assertThrows(DataAccessException.class, () -> dao.updateOrCreate(new AuthToken("u1", null)));
    }

    @Test
    void testGetAuthTokenSucceeds() throws DataAccessException {
        AuthToken token1 = DatabaseFactory.createAuthToken("token1", "user1");
        AuthToken token2 = DatabaseFactory.createAuthToken("token2", "user2");
        AuthTokenDAO dao = new AuthTokenDAO();
        Assertions.assertEquals(token1, dao.getAuthToken("token1"));
        Assertions.assertEquals(token2, dao.getAuthToken("token2"));
    }

    @Test
    void testFailGetAuthTokenReturnNull() throws DataAccessException {
        AuthTokenDAO dao = new AuthTokenDAO();
        Assertions.assertNull(dao.getAuthToken("I Dont exist"));
        Assertions.assertNull(dao.getAuthToken("Me neither"));
    }

    @Test
    void testDeleteAuthTokenSucceeds() throws DataAccessException {
        AuthToken token1 = DatabaseFactory.createAuthToken("token1", "user1");
        AuthToken token2 = DatabaseFactory.createAuthToken("token2", "user2");
        AuthTokenDAO dao = new AuthTokenDAO();

        Assertions.assertDoesNotThrow(() -> dao.delete(token1));
        Assertions.assertEquals(1, DatabaseFactory.countAuthTokens());
        Assertions.assertDoesNotThrow(() -> dao.delete(token2));
        Assertions.assertEquals(0, DatabaseFactory.countAuthTokens());
    }

    @Test
    void testFailDeleteAuthTokenWhenNonExistent() throws DataAccessException {
        AuthToken empty = new AuthToken(null, null);
        AuthToken mock = new AuthToken("Crazy", "Token");

        AuthTokenDAO dao = new AuthTokenDAO();

        Assertions.assertDoesNotThrow(() -> dao.delete(empty));
        Assertions.assertEquals(0, DatabaseFactory.countAuthTokens());
        Assertions.assertDoesNotThrow(() -> dao.delete(mock));
        Assertions.assertEquals(0, DatabaseFactory.countAuthTokens());
    }
}
