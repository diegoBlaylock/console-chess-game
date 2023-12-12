package myTests.servicesTests;

import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.exceptions.UnauthorizedException;
import edu.blaylock.server.handlers.HandlerUtils;
import edu.blaylock.server.services.RequestUtils;
import myTests.resources.DatabaseFactory;
import myTests.resources.RequestFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {
    @BeforeAll
    static void setupDatabase() throws Exception {
        DatabaseFactory.setupDatabase();
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException {
        ServerGlobals.database().dropDatabase();
    }

    @Test
    void testAreAnyEmptyFindEmptyStrings() {
        String valid = "Valid";
        String invalidEmpty = "";

        assertTrue(RequestUtils.areAnyEmpty(valid, invalidEmpty, null));
        assertTrue(RequestUtils.areAnyEmpty(valid, invalidEmpty));
        assertTrue(RequestUtils.areAnyEmpty(valid, null));
        assertFalse(RequestUtils.areAnyEmpty(valid));
    }

    @Test
    void testCheckAuthorizationSucceeds() throws DataAccessException {
        DatabaseFactory.createAuthToken("token", "u1");
        Request request = RequestFactory.newRequest("", "authorization:token");

        assertDoesNotThrow(() -> HandlerUtils.checkAuthorization(request), "Check authorization throws error when it shouldn't");
    }

    @Test
    void testFailCheckAuthorizationThrowsUnauthorized() {
        Request emptyRequest = RequestFactory.newRequest("");
        Request invalidRequest = RequestFactory.newRequest("authorization:token");


        assertThrows(UnauthorizedException.class, () -> HandlerUtils.checkAuthorization(emptyRequest), "checkAuthorization succeeds");
        assertThrows(UnauthorizedException.class, () -> HandlerUtils.checkAuthorization(invalidRequest), "checkAuthorization succeeds");
    }
}
