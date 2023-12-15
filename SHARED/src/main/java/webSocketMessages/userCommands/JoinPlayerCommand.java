package webSocketMessages.userCommands;

import chess.ChessGame;

/**
 * Sent from client to start a connection and register a session with a user for playing a game
 */
public class JoinPlayerCommand extends UserGameCommand {
    private final ChessGame.TeamColor playerColor;
    
    public JoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken, CommandType.JOIN_PLAYER, gameID);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor playerColor() {
        return playerColor;
    }
}
