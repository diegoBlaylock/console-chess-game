package edu.blaylock.server;


import edu.blaylock.server.exceptions.DataAccessException;
import spark.Spark;

/**
 * Representation of the server. Has function to run server
 */
public class ChessServer {

    /**
     * Setup server resources and run server
     *
     * @param args Ignored
     */
    public static void main(String[] args) {
        try {
            ServerGlobals.setupGlobals(new ChessServer());
        } catch (DataAccessException e) {
            System.out.println("Couldn't setup globals, most likely error with database");
            System.exit(-1);
        }
        ServerGlobals.server().run(8080);
    }

    /**
     * Run server on specified port, setup database, routes, and static files
     *
     * @param port What port to run the server on
     */
    public void run(int port) {
        try {


            Spark.port(port);
            ServerGlobals.handlers().registerHandlers();

        } catch (Exception e) {
            System.out.println("Error occurred upon startup" + e);
            System.exit(-1);
        }
    }
}
