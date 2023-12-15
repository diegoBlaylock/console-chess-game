package edu.blaylock.server.exceptions;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    /**
     * Classify a database exception
     *
     * @param message reason
     */
    public DataAccessException(String message) {
        super(message);
    }
}
