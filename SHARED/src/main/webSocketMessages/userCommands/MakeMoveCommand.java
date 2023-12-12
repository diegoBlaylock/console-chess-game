package webSocketMessages.userCommands;

import chess.ChessMove;

/**
 * Attempt by the client to make a move. Many error messages can result if the client isn't the right player or the move
 * is incorrect.
 */
public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;

    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        super(authToken, CommandType.MAKE_MOVE, gameID);
        this.move = move;
    }

    public ChessMove move() {
        return move;
    }
}
