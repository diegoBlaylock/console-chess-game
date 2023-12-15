package edu.blaylock.server.services.game.requests;

import edu.blaylock.server.exceptions.BadRequestException;
import edu.blaylock.server.services.RequestUtils;
import edu.blaylock.utils.gson.GsonUtils;
import spark.Request;

/**
 * Wrapper for a game name
 *
 * @param gameName String gameName
 */
public record CreateGameRequest(String gameName) {

    /**
     * Read an HTTP request and fill in nessecary data for service
     *
     * @param request http request with info
     * @return populated CreateGameRequest
     * @throws BadRequestException if request body isn't a json dict or if it lacks "gameName" key
     */
    public static CreateGameRequest getRequest(Request request) throws BadRequestException {
        CreateGameRequest createGameRequest = GsonUtils.standard().fromJson(request.body(), CreateGameRequest.class);
        if (createGameRequest == null || RequestUtils.areAnyEmpty(createGameRequest.gameName()))
            throw new BadRequestException();
        return createGameRequest;
    }
}
