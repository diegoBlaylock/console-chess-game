package edu.blaylock.server.database.models;

import java.util.UUID;

/**
 * Model describing authToken and username from database
 *
 * @param authToken String token
 * @param username  String username
 */
public record AuthToken(String authToken, String username) implements IModel {

    /**
     * Generate new AuthToken model with randomized token and given name
     *
     * @param name username
     * @return new model
     */
    public static AuthToken generateRandom(String name) {
        return new AuthToken(UUID.randomUUID().toString(), name);
    }
}
