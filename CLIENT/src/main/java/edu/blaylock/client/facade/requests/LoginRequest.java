package edu.blaylock.client.facade.requests;

/**
 * Wrapper for a user and given password
 *
 * @param username User username
 * @param password User password
 */
public record LoginRequest(String username, String password) {
}
