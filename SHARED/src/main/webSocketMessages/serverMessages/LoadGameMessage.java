package webSocketMessages.serverMessages;

import chess.ChessGame;
import edu.blaylock.chess.GameState;

/**
 * Sent from Server to Client with updated ChessGame to be displayed
 */
public class LoadGameMessage extends ServerMessage {

    private final ChessGame game;
    private final GameState state;
    private final String name;

    /**
     * Create Message
     *
     * @param game  Updated Game
     * @param state Record State for the Game (unfinished, ended, resigned)
     * @param name  Name of game
     */
    public LoadGameMessage(ChessGame game, GameState state, String name) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.state = state;
        this.name = name;
    }

    public ChessGame game() {
        return game;
    }

    public GameState state() {
        return state;
    }

    public String name() {
        return name;
    }
}
