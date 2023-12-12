package edu.blaylock.server.handlers;

import edu.blaylock.server.exceptions.AlreadyTakenException;
import edu.blaylock.server.exceptions.BadRequestException;
import edu.blaylock.server.exceptions.UnauthorizedException;
import edu.blaylock.utils.Status;
import edu.blaylock.utils.gson.GsonUtils;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * Sets up and handles all Exceptions in the server.
 */
public class ExceptionHandlers {

    /**
     * Set all handlers, AlreadyTakenException, BadRequestException, UnauthorizedException, All Exceptions
     */
    public void registerHandlers() {
        Spark.exception(AlreadyTakenException.class, new ExHandler(Status.ALREADY_TAKEN)::handle);
        Spark.exception(BadRequestException.class, new ExHandler(Status.BAD_REQUEST)::handle);
        Spark.exception(UnauthorizedException.class, new ExHandler(Status.UNAUTHORIZED)::handle);
        Spark.exception(Exception.class, new ExHandler(Status.ERROR)::handle);

    }

    /**
     * Temporary class to be serialized by GSON in form '{"message": &lt;message&gt;}'
     *
     * @param message message to serialize
     */
    protected record Message(String message) {
    }

    /**
     * General Handler for exceptions. Will return Response with given status code and exceptions message.
     * Body formatted as '{"message": "Error: &lt;exception message&gt;"}
     */
    protected static class ExHandler {
        /**
         * Status to send in response
         */
        private final int status;

        /**
         * New ExHandler which sends a message with given status of an exception
         *
         * @param status Status to respond with
         */
        ExHandler(int status) {
            this.status = status;
        }

        /**
         * Routing function used by the exception handler
         */
        private void handle(Exception e, Request req, Response res) {
            Message message = new Message("Error: " + e.getMessage());

            res.status(status);
            res.body(GsonUtils.standard().toJson(message));
        }
    }
}
