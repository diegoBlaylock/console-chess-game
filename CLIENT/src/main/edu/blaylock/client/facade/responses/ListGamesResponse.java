package edu.blaylock.client.facade.responses;

/**
 * Temporary record for an array of games.
 *
 * @param games array of Games to store
 */
public record ListGamesResponse(GameDescription[] games) {
    public record GameDescription(int gameID, String whiteUsername, String blackUsername, String gameName) {
    }
}
