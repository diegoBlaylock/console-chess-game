package edu.blaylock.client.facade.responses;

/**
 * Wrapper around an authToken
 */
public record AuthTokenResponse(String authToken, String username) {

}
