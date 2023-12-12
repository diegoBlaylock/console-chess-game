package webSocketMessages.userCommands;

/**
 * Sent by player client to resign.
 */
public class ResignCommand extends UserGameCommand {
    public ResignCommand(String authToken, int gameID) {
        super(authToken, CommandType.RESIGN, gameID);
    }
}
