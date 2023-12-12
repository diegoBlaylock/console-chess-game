package webSocketMessages.userCommands;

/**
 * Should be sent by a client to indicate that they will leave. Same thing will happen however if the connection is closed.
 */
public class LeaveCommand extends UserGameCommand {
    public LeaveCommand(String authToken, int gameID) {
        super(authToken, CommandType.LEAVE, gameID);
    }
}
