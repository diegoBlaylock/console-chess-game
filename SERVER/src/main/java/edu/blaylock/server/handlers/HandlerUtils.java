package edu.blaylock.server.handlers;

import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.exceptions.UnauthorizedException;
import edu.blaylock.server.services.user.UserUtils;
import spark.Request;

/**
 * Class for utilities used by the handler classes
 */
public class HandlerUtils {


    /**
     * Takes in a request and make sure that: (a) the authorization header exists; (b) the authorization token is valid
     * Otherwise throw Unauthorized exception
     *
     * @param request HTTPRequest to validate
     * @throws UnauthorizedException Request did not meet specification laid out above.
     */
    public static void checkAuthorization(Request request) throws UnauthorizedException {
        String token = request.headers("authorization");
        if (token == null || !UserUtils.doesAuthTokenExist(token)) {
            throw new UnauthorizedException();
        }
    }

    /**
     * Drop all data from global database
     */
    public static void dropDatabase() throws DataAccessException {
        ServerGlobals.database().dropDatabase();
    }

    /**
     * All routes must return at least this message
     */
    public static final String EMPTY_BODY = "{}";

}
