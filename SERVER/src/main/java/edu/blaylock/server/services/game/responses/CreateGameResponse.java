package edu.blaylock.server.services.game.responses;

import edu.blaylock.utils.gson.GsonUtils;

/**
 * Wrapper for a gameID
 *
 * @param gameID int id
 */
public record CreateGameResponse(int gameID) {

    /**
     * Return String representation of this class
     *
     * @return json dict with key "gameID"
     */
    public String createResponseBody() {
        return GsonUtils.standard().toJson(this);
    }
}
