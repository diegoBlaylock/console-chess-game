package edu.blaylock.server.exceptions;

/**
 * Thrown if there is a lack of authorization in some server-client exchange
 */
public class UnauthorizedException extends Exception {
    /**
     * Sets message to "unauthorized"
     */
    public UnauthorizedException() {
        super("unauthorized");
    }
}
