package edu.blaylock.server.services.user.requests;

import edu.blaylock.server.exceptions.BadRequestException;
import edu.blaylock.server.services.RequestUtils;
import edu.blaylock.utils.gson.GsonUtils;
import spark.Request;

/**
 * A wrapper for a username, password, and email
 *
 * @param username User username
 * @param password User password
 * @param email    User email
 */
public record RegisterRequest(String username, String password, String email) {

    /**
     * Reads an HTTP request and populates an instance of RegisterRequest with data
     *
     * @param request an http request
     * @return A populated RegisterRequest
     * @throws BadRequestException thrown if request body is not json or lacks required keys
     */
    public static RegisterRequest getRequest(Request request) throws BadRequestException {
        RegisterRequest registerRequest = GsonUtils.standard().fromJson(request.body(), RegisterRequest.class);

        if (RequestUtils.areAnyEmpty(registerRequest.username(), registerRequest.password(), registerRequest.email()))
            throw new BadRequestException();
        return registerRequest;
    }
}
