package edu.blaylock.server.services.user;

import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.database.dao.AuthTokenDAO;
import edu.blaylock.server.database.dao.UserDAO;
import edu.blaylock.server.database.models.AuthToken;
import edu.blaylock.server.database.models.User;
import edu.blaylock.server.exceptions.AlreadyTakenException;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.exceptions.UnauthorizedException;
import edu.blaylock.server.services.user.requests.LoginRequest;
import edu.blaylock.server.services.user.requests.LogoutRequest;
import edu.blaylock.server.services.user.requests.RegisterRequest;
import edu.blaylock.server.services.user.responses.AuthTokenResponse;

/**
 * Class containing service methods pertaining to Users and Sessions
 */
public class UserService {

    /**
     * Register a new User in database. Name suggested needs to be unique and will throw an error if
     * this constraint fails.
     *
     * @param request Wrapper for a username, password, and email
     * @return RegisterResponse containing an AuthToken
     * @throws AlreadyTakenException Thrown if username already exists
     * @throws DataAccessException   Database error
     */
    public static AuthTokenResponse registerUser(RegisterRequest request)
            throws AlreadyTakenException, DataAccessException {
        UserDAO userDAO = new UserDAO();
        AuthTokenDAO authDAO = new AuthTokenDAO();

        if (userDAO.getUserByName(request.username()) != null) throw new AlreadyTakenException();

        String hashedPassword = UserUtils.saltedHash(request.password());
        User proposedUser = new User(request.username(), hashedPassword, request.email());
        AuthToken authToken = AuthToken.generateRandom(proposedUser.name());
        userDAO.create(proposedUser);
        authDAO.updateOrCreate(authToken);

        return new AuthTokenResponse(authToken);
    }

    /**
     * Creates and returns an AuthToken for an existing user
     *
     * @param request Wrapper for Username and Password
     * @return Wrapper for AuthToken
     * @throws UnauthorizedException Thrown if password given doesn't match the password stored or User doesn't exist
     * @throws DataAccessException   Database error
     */
    public static AuthTokenResponse login(LoginRequest request) throws UnauthorizedException, DataAccessException {
        User userModel = new UserDAO(ServerGlobals.database()).getUserByName(request.username());

        if (userModel == null || !UserUtils.passwordsMatch(request.password(), userModel.password()))
            throw new UnauthorizedException();

        AuthTokenDAO authDAO = new AuthTokenDAO();
        AuthToken newToken = AuthToken.generateRandom(userModel.name());
        authDAO.updateOrCreate(newToken);
        return new AuthTokenResponse(newToken);
    }

    /**
     * Deletes an AuthToken for the given User. Validation of token should occur in handler.
     *
     * @param request Wrapper for an AuthToken
     * @throws DataAccessException Database error
     */
    public static void logout(LogoutRequest request) throws DataAccessException, UnauthorizedException {
        AuthTokenDAO authDAO = new AuthTokenDAO();
        AuthToken authToken = authDAO.getAuthToken(request.authToken());
        if (authToken == null) throw new UnauthorizedException();
        authDAO.delete(authToken);
    }

}
