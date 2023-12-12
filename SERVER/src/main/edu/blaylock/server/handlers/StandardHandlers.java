package edu.blaylock.server.handlers;

import edu.blaylock.server.exceptions.AlreadyTakenException;
import edu.blaylock.server.exceptions.BadRequestException;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.exceptions.UnauthorizedException;
import edu.blaylock.server.gameplay.GameManager;
import edu.blaylock.server.services.game.GameService;
import edu.blaylock.server.services.game.requests.CreateGameRequest;
import edu.blaylock.server.services.game.requests.JoinGameRequest;
import edu.blaylock.server.services.game.responses.CreateGameResponse;
import edu.blaylock.server.services.game.responses.ListGamesResponse;
import edu.blaylock.server.services.user.UserService;
import edu.blaylock.server.services.user.requests.LoginRequest;
import edu.blaylock.server.services.user.requests.LogoutRequest;
import edu.blaylock.server.services.user.requests.RegisterRequest;
import edu.blaylock.server.services.user.responses.AuthTokenResponse;
import edu.blaylock.utils.Status;
import spark.Request;
import spark.Response;
import spark.Spark;

import static edu.blaylock.server.handlers.HandlerUtils.checkAuthorization;

/**
 * Class for all routing handlers
 */
public class StandardHandlers {

    public void registerHandlers() {
        Spark.delete("/db", this::clearApplication);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
    }

    /**
     * Clear database
     */
    protected String clearApplication(Request req, Response res) throws DataAccessException {
        HandlerUtils.dropDatabase();
        GameManager.clear();
        res.status(Status.SUCCESS);
        return HandlerUtils.EMPTY_BODY;
    }

    protected String registerUser(Request req, Response res)
            throws BadRequestException, AlreadyTakenException, DataAccessException {
        RegisterRequest serviceRequest = RegisterRequest.getRequest(req);
        AuthTokenResponse serviceResponse = UserService.registerUser(serviceRequest);

        res.status(Status.SUCCESS);
        return serviceResponse.createResponseBody();
    }

    protected String loginUser(Request req, Response res)
            throws BadRequestException, UnauthorizedException, DataAccessException {
        LoginRequest serviceRequest = LoginRequest.getRequest(req);
        AuthTokenResponse serviceResponse = UserService.login(serviceRequest);

        res.status(Status.SUCCESS);
        return serviceResponse.createResponseBody();
    }

    protected String logoutUser(Request req, Response res) throws UnauthorizedException, DataAccessException {
        checkAuthorization(req);

        LogoutRequest serviceRequest = LogoutRequest.getRequest(req);
        UserService.logout(serviceRequest);

        res.status(Status.SUCCESS);
        return HandlerUtils.EMPTY_BODY;
    }

    protected String listGames(Request req, Response res) throws UnauthorizedException, DataAccessException {
        checkAuthorization(req);

        ListGamesResponse serviceResponse = GameService.getGamesList();
        res.status(Status.SUCCESS);
        return serviceResponse.createResponseBody();
    }

    protected String createGame(Request req, Response res)
            throws BadRequestException, UnauthorizedException, DataAccessException {
        checkAuthorization(req);

        CreateGameRequest serviceRequest = CreateGameRequest.getRequest(req);
        CreateGameResponse serviceResponse = GameService.createGame(serviceRequest);

        res.status(Status.SUCCESS);
        return serviceResponse.createResponseBody();
    }

    protected String joinGame(Request req, Response res)
            throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        checkAuthorization(req);

        JoinGameRequest serviceRequest = JoinGameRequest.getRequest(req);
        GameService.joinGame(serviceRequest);

        res.status(Status.SUCCESS);
        return HandlerUtils.EMPTY_BODY;
    }
}
