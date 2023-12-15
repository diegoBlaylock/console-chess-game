package edu.blaylock.client.facade.exceptions;

/**
 * Error connecting to server
 */
public class ConnectionException extends Exception {
    public ConnectionException(String message) {
        super(message);
    }
}
