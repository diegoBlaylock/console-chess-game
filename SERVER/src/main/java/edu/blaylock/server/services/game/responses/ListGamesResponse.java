package edu.blaylock.server.services.game.responses;

import edu.blaylock.server.database.models.Game;
import edu.blaylock.utils.gson.GsonUtils;

/**
 * Temporary record for an array of games.
 *
 * @param games array of Games to store
 */
public record ListGamesResponse(Game[] games) {

    /**
     * Create json string
     *
     * @return json dict with key "games" and value as list of games
     */
    public String createResponseBody() {
        return GsonUtils.allowNulls().toJson(this);
    }
}
