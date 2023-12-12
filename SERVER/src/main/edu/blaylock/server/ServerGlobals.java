package edu.blaylock.server;

import edu.blaylock.server.database.implementations.IDatabase;
import edu.blaylock.server.database.implementations.mysql.MySqlDatabase;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.handlers.HandlerManager;

/**
 * Static class for all singletons needed in the server program
 */
public class ServerGlobals {
    /**
     * Keeps track of current server
     */
    private static ChessServer runningServerInstance;

    /**
     * Current handlers
     */
    private static HandlerManager handlerManagerInstance;

    /**
     * Current database (Needs to be changed in phase 4)
     */
    private static IDatabase databaseInstance;

    /**
     * Set Server, Database, and HandlerManager globals
     *
     * @param server Server to save
     */
    public static void setupGlobals(ChessServer server) throws DataAccessException {
        ServerGlobals.server(server);
        ServerGlobals.database(new MySqlDatabase());
        ServerGlobals.handlers(new HandlerManager());
    }

    public static IDatabase database() {
        return databaseInstance;
    }

    public static HandlerManager handlers() {
        return handlerManagerInstance;
    }

    public static ChessServer server() {
        return runningServerInstance;
    }

    public static void database(IDatabase database) {
        ServerGlobals.databaseInstance = database;
    }

    public static void handlers(HandlerManager handlers) {
        ServerGlobals.handlerManagerInstance = handlers;
    }

    public static void server(ChessServer server) {
        ServerGlobals.runningServerInstance = server;
    }
}
