package webSocketMessages.userCommands;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public UserGameCommand(String authToken, CommandType type, int gameID) {
        this.authToken = authToken;
        this.commandType = type;
        this.gameID = gameID;
    }

    public enum CommandType {
        JOIN_PLAYER(JoinPlayerCommand.class),
        JOIN_OBSERVER(JoinObserverCommand.class),
        MAKE_MOVE(MakeMoveCommand.class),
        LEAVE(LeaveCommand.class),
        RESIGN(ResignCommand.class),
        VERBOSE(VerboseCommand.class);

        private final Class<? extends UserGameCommand> clazz;

        CommandType(Class<? extends UserGameCommand> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends UserGameCommand> commandClass() {
            return clazz;
        }
    }

    protected CommandType commandType;

    private final String authToken;

    private final int gameID;

    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    public int gameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }
}
