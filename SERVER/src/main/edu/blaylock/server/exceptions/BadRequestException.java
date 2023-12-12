package edu.blaylock.server.exceptions;

/**
 * Thrown if the request body doesn't have the required parameters or those parameters are invalid
 */
public class BadRequestException extends Exception {
    /**
     * Sets message to "bad request"
     */
    public BadRequestException() {
        super("bad request");
    }
}
