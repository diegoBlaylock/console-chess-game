package edu.blaylock.utils;

/**
 * Status code used by server.
 */
public final class Status {
    /**
     * Successful response
     */
    public static final int SUCCESS = 200;

    /**
     * Already taken exception
     */
    public static final int ALREADY_TAKEN = 403;

    /**
     * Bad request exception
     */
    public static final int BAD_REQUEST = 400;

    /**
     * Unauthorized Exception
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * Server error
     */
    public static final int ERROR = 500;
}
