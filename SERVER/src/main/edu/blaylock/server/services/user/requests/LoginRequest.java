package edu.blaylock.server.services.user.requests;

import edu.blaylock.server.exceptions.BadRequestException;
import edu.blaylock.server.services.RequestUtils;
import edu.blaylock.utils.gson.GsonUtils;
import spark.Request;

/**
 * Wrapper for a user and given password
 *
 * @param username User username
 * @param password User password
 */
public record LoginRequest(String username, String password) {

    /**
     * Read data from an http request
     *
     * @param request An http request
     * @return LoginRequest with populated fields
     * @throws BadRequestException Thrown if request body doesn't match specification or fields empty
     */
    public static LoginRequest getRequest(Request request) throws BadRequestException {
        LoginRequest loginRequest = GsonUtils.standard().fromJson(request.body(), LoginRequest.class);
        if (RequestUtils.areAnyEmpty(loginRequest.username(), loginRequest.password()))
            throw new BadRequestException();

        return loginRequest;
    }
}
