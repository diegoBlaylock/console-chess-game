package edu.blaylock.server.services.user;

import edu.blaylock.server.database.dao.AuthTokenDAO;
import edu.blaylock.server.exceptions.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Contains various methods needed by User services
 */
public class UserUtils {

    public final static BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

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

    public static String saltedHash(String password) {
        if (password == null) return null;
        return ENCODER.encode(password);
    }

    public static boolean passwordsMatch(String password, String encoded) {
        if (password == null) return false;
        return ENCODER.matches(password, encoded);
    }

}
