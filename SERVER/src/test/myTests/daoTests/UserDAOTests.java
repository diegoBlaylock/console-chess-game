package myTests.daoTests;

import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.database.dao.UserDAO;
import edu.blaylock.server.database.models.User;
import edu.blaylock.server.exceptions.DataAccessException;
import myTests.resources.DatabaseFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDAOTests {

    @BeforeAll
    static void setupDatabase() throws Exception {
        DatabaseFactory.setupDatabase();
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException {
        ServerGlobals.database().dropDatabase();
    }

    @Test
    void testCreateUserSuccessful() {
        Assertions.assertDoesNotThrow(() -> DatabaseFactory.createUser("u1", "p1", "e1"), "Create fails");
    }

    @Test
    void testFailCreateUserWhenNameOrPasswordNull() {
        Assertions.assertThrows(DataAccessException.class, () -> DatabaseFactory.createUser(null, "p1", "e1"));
        Assertions.assertThrows(DataAccessException.class, () -> DatabaseFactory.createUser("u1", null, "e1"));
    }

    @Test
    void testFailCreateUserNameNotUnique() throws DataAccessException {
        DatabaseFactory.createUser("u1", "p1", "e1");
        Assertions.assertThrows(DataAccessException.class, () -> DatabaseFactory.createUser("u1", "p2", "e2"));
    }

    @Test
    void testGetUserByNameSuccessful() throws DataAccessException {
        User user1 = DatabaseFactory.createUser("u1", "p1", "e1");
        User user2 = DatabaseFactory.createUser("u2", "p2", "e2");

        UserDAO dao = new UserDAO();

        Assertions.assertEquals(user1, dao.getUserByName("u1"));
        Assertions.assertEquals(user2, dao.getUserByName("u2"));
    }

    @Test
    void testFailGetUserByNameWhenNotExistentShouldReturnNull() throws DataAccessException {
        UserDAO dao = new UserDAO();
        Assertions.assertNull(dao.getUserByName("I don't exist"));
        Assertions.assertNull(dao.getUserByName("I don't either"));
    }
}
