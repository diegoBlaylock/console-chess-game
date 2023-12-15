package myTests.servicesTests;

import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.database.dao.AuthTokenDAO;
import edu.blaylock.server.exceptions.AlreadyTakenException;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.exceptions.UnauthorizedException;
import edu.blaylock.server.services.user.UserService;
import edu.blaylock.server.services.user.UserUtils;
import edu.blaylock.server.services.user.requests.LoginRequest;
import edu.blaylock.server.services.user.requests.LogoutRequest;
import edu.blaylock.server.services.user.requests.RegisterRequest;
import edu.blaylock.server.services.user.responses.AuthTokenResponse;
import myTests.resources.DatabaseFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @BeforeAll
    static void setupDatabase() throws Exception {
        DatabaseFactory.setupDatabase();
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException {
        ServerGlobals.database().dropDatabase();
    }

    @Test
    void testRegisterUserSucceeds() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("u1", "p1", "e1");
        assertDoesNotThrow(() -> UserService.registerUser(request), "Exception thrown");
        assertEquals(1, DatabaseFactory.countUsers());
    }

    @Test
    void testFailRegisterUserThrowsAlreadyTaken() throws DataAccessException {
        DatabaseFactory.createUser("u1", "p1", "e1");
        RegisterRequest request = new RegisterRequest("u1", "p1", "e1");
        assertThrows(AlreadyTakenException.class, () -> UserService.registerUser(request), "Exception thrown");
    }

    @Test
    void testLoginSucceeds() throws DataAccessException, UnauthorizedException {
        DatabaseFactory.createUser("u1", "p1", "e1");

        LoginRequest request = new LoginRequest("u1", "p1");
        AuthTokenResponse response = UserService.login(request);
        assertEquals(1, DatabaseFactory.countAuthTokens());
        assertEquals("u1", new AuthTokenDAO(ServerGlobals.database()).getAuthToken(response.authToken().authToken()).username(),
                "Couldn't find id");
    }

    @Test
    void testFailLoginUnauthorized() throws DataAccessException {
        DatabaseFactory.createUser("u1", "p1", "e1");

        LoginRequest request = new LoginRequest("u1", "wrongPassword");
        assertThrows(UnauthorizedException.class, () -> UserService.login(request), "Exception not thrown");
    }

    @Test
    void testLogoutSucceeds() throws DataAccessException, UnauthorizedException {
        DatabaseFactory.createUser("u1", "p1", "e1");
        DatabaseFactory.createAuthToken("token", "u1");
        LogoutRequest request = new LogoutRequest("token");
        UserService.logout(request);
        assertEquals(0, DatabaseFactory.countAuthTokens());
    }

    @Test
    void testFailLogoutUnauthorized() throws DataAccessException {
        DatabaseFactory.createAuthToken("token", "u1");
        assertFalse(UserUtils.doesAuthTokenExist("wrong_token"), "Token doesn't exist");
        assertThrows(UnauthorizedException.class, () -> UserService.logout(new LogoutRequest("wrong_token")), "Exception not thrown");
    }
}
