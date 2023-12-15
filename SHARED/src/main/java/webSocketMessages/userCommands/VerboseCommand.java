package webSocketMessages.userCommands;

/**
 * For the UI I want to design, I need the server to notify more directly that someone has resigned so that the client
 * can update it's own state. This message tells the server that this client wants to be sent loadGames more (which
 * contain the state info) so that I can design my UI and still pass the websocket tests.
 */
public class VerboseCommand extends UserGameCommand {
    public VerboseCommand(String authToken, int gameID) {
        super(authToken, CommandType.VERBOSE, gameID);
    }
}
