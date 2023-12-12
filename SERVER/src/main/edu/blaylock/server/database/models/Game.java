package edu.blaylock.server.database.models;

import chess.ChessGame;
import edu.blaylock.chess.GameState;
import edu.blaylock.utils.gson.Transient;

/**
 * A record that keeps track of game data.
 *
 * @param gameID        unique ID that identifies a game instance
 * @param whiteUsername name of white player
 * @param blackUsername name of black player
 * @param gameName      name of game
 * @param game          serializable ChessGame
 * @param state         state of the game
 */
public record Game(int gameID, String whiteUsername, String blackUsername, String gameName,
                   @Transient ChessGame game, @Transient GameState state) implements IModel {

    public String username(ChessGame.TeamColor color) {
        return switch (color) {
            case WHITE -> whiteUsername();
            case BLACK -> blackUsername();
        };
    }
}
