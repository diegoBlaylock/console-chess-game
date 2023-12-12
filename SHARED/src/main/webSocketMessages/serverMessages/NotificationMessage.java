package webSocketMessages.serverMessages;

/**
 * Sent from server to client with a message to display.
 */
public class NotificationMessage extends ServerMessage {

    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String message() {
        return message;
    }
}
