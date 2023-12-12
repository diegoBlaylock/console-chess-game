package edu.blaylock.server.exceptions;

/**
 * Thrown whenever the uniqueness constrain on a value fails
 */
public class AlreadyTakenException extends Exception {
    /**
     * Sets message to "already taken"
     */
    public AlreadyTakenException() {
        super("already taken");
    }
}
