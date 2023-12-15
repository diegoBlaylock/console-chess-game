package webSocketMessages.userCommands;

/**
 * Sent from client to start a connection and register a session with a user for observing a game
 */
public class JoinObserverCommand extends UserGameCommand {
    public JoinObserverCommand(String authToken, int gameID) {
        super(authToken, CommandType.JOIN_OBSERVER, gameID);
    }
}
