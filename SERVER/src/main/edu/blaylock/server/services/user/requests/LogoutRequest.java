package edu.blaylock.server.services.user.requests;

import spark.Request;

/**
 * Wrapper for an AuthToken
 *
 * @param authToken String token
 */
public record LogoutRequest(String authToken) {

    /**
     * Reads an http request and returns a populated LogoutRequest
     *
     * @param request An http request
     * @return A populated LogoutRequest
     */
    public static LogoutRequest getRequest(Request request) {
        return new LogoutRequest(request.headers("authorization"));
    }
}
