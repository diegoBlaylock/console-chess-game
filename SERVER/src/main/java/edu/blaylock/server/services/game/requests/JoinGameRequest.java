package edu.blaylock.server.services.game.requests;

import edu.blaylock.server.exceptions.BadRequestException;
import edu.blaylock.utils.gson.GsonUtils;
import spark.Request;

/**
 * Wrapper for a token, playerColor, and gameID
 *
 * @param authToken   String token
 * @param playerColor String player color
 * @param gameID      int id
 */
public record JoinGameRequest(String authToken, String playerColor, int gameID) {

    /**
     * Reads an HTTP request and populates a JoinGameRequest with nessecary data
     *
     * @param request An http request
     * @return populated JoinGameRequest
     * @throws BadRequestException Thrown if request body not json, lacks parameters, or gameID is 0
     */
    public static JoinGameRequest getRequest(Request request) throws BadRequestException {
        JoinGameRequest joinGameRequest = GsonUtils.standard().fromJson(request.body(), JoinGameRequest.class);
        if (joinGameRequest.gameID == 0)
            throw new BadRequestException();
        return new JoinGameRequest(request.headers("authorization"), joinGameRequest.playerColor, joinGameRequest.gameID);
    }

}
