package webSocketMessages.serverMessages;

/**
 * Used to communicate an error from client to server.
 */
public class ErrorMessage extends ServerMessage {

    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String errorMessage() {
        return errorMessage;
    }
}
