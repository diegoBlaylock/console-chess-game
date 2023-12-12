package edu.blaylock.server.services.user;

import edu.blaylock.server.database.dao.AuthTokenDAO;
import edu.blaylock.server.exceptions.DataAccessException;

/**
 * Contains various methods needed by User services
 */
public class UserUtils {

    /**
     * Checks if the given token exists in Database
     *
     * @param authToken Token to check
     * @return Whether a model was found
     */
    public static boolean doesAuthTokenExist(String authToken) {
        try {
            return new AuthTokenDAO().getAuthToken(authToken) != null;
        } catch (DataAccessException e) {
            return false;
        }
    }

}
