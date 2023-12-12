package edu.blaylock.client.facade.requests;

/**
 * A wrapper for a username, password, and email
 *
 * @param username User username
 * @param password User password
 * @param email    User email
 */
public record RegisterRequest(String username, String password, String email) {
}
