package edu.blaylock.server.services.user.responses;

import edu.blaylock.server.database.models.AuthToken;
import edu.blaylock.utils.gson.GsonUtils;

/**
 * Wrapper around an authToken
 *
 * @param authToken AuthToken token
 */
public record AuthTokenResponse(AuthToken authToken) {

    /**
     * generate json string
     *
     * @return json dict with username and authtoken from authtoken model
     */
    public String createResponseBody() {
        return GsonUtils.standard().toJson(authToken);
    }
}
