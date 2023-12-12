package edu.blaylock.server.handlers;

/**
 * Parent handler that sets up HTTP handlers for routes as well as exceptions
 */
public class HandlerManager {
    /**
     * Handler for Routes
     */
    StandardHandlers standardHandlers = new StandardHandlers();
    /**
     * Handler for local exceptions
     */
    ExceptionHandlers exceptionHandlers = new ExceptionHandlers();

    WSHandlers gameplayHandlers = new WSHandlers();

    /**
     * Register all server handlers
     */
    public void registerHandlers() {
        gameplayHandlers.registerHandlers();

        exceptionHandlers.registerHandlers();
        standardHandlers.registerHandlers();
    }

}
