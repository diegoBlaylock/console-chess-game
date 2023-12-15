package edu.blaylock.client.facade.requests;

/**
 * Wrapper for a token, playerColor, and gameID
 *
 * @param playerColor String player color
 * @param gameID      int id
 */
public record JoinGameRequest(String playerColor, int gameID) {
}
