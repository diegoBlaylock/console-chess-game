package edu.blaylock.client.facade;

import com.google.gson.Gson;
import edu.blaylock.client.Main;
import edu.blaylock.client.facade.exceptions.ConnectionException;
import edu.blaylock.client.facade.exceptions.ServerException;
import edu.blaylock.client.facade.requests.CreateGameRequest;
import edu.blaylock.client.facade.requests.JoinGameRequest;
import edu.blaylock.client.facade.requests.LoginRequest;
import edu.blaylock.client.facade.requests.RegisterRequest;
import edu.blaylock.client.facade.responses.AuthTokenResponse;
import edu.blaylock.client.facade.responses.CreateGameResponse;
import edu.blaylock.client.facade.responses.ListGamesResponse;
import edu.blaylock.utils.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Holds all the methods for the api endpoints. Websockets are managed through the GameConnection object and are
 * obtained through the connect method
 */
public class ServerFacade {

    public enum QueryMethod {
        GET, POST, PUT, DELETE;
    }

    URI url;
    String token;

    /**
     * Create new Server Facade with the given url
     *
     * @param url url to connect to
     * @throws URISyntaxException Url badly formed
     */
    public ServerFacade(String url) throws URISyntaxException {
        this.url = new URI(url);
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/3-web-api/web-api.md">
     * Spec
     * </a>
     */
    public void registerUser(String username, String password, String email) throws ServerException, ConnectionException {
        RegisterRequest request = new RegisterRequest(username, password, email);

        AuthTokenResponse response = queryServer("user", QueryMethod.POST, request, AuthTokenResponse.class);
        assert response != null;
        token = response.authToken();
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/3-web-api/web-api.md">
     * Spec
     * </a>
     */
    public void login(String username, String password) throws ServerException, ConnectionException {
        LoginRequest request = new LoginRequest(username, password);
        AuthTokenResponse response = queryServer("session", QueryMethod.POST, request, AuthTokenResponse.class);
        assert response != null;
        token = response.authToken();
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/3-web-api/web-api.md">
     * Spec
     * </a>
     */
    public void logout() throws ServerException, ConnectionException {
        queryServer("session", QueryMethod.DELETE, null, null);
        token = null;
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/3-web-api/web-api.md">
     * Spec
     * </a>
     */
    public ListGamesResponse listGames() throws ServerException, ConnectionException {
        return queryServer("game", QueryMethod.GET, null, ListGamesResponse.class);
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/3-web-api/web-api.md">
     * Spec
     * </a>
     */
    public CreateGameResponse createGame(String gameName) throws ServerException, ConnectionException {
        CreateGameRequest request = new CreateGameRequest(gameName);
        return queryServer("game", QueryMethod.POST, request, CreateGameResponse.class);
    }

    /**
     * Refer to
     * <a href="https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/3-web-api/web-api.md">
     * Spec
     * </a>
     */
    public void joinGame(String color, int gameId) throws ServerException, ConnectionException {
        JoinGameRequest request = new JoinGameRequest(color, gameId);
        queryServer("game", QueryMethod.PUT, request, null);
    }

    /**
     * NEVER USED IN PRODUCTION, BUT NESSECARY FOR TESTS
     */
    public void clearDB() throws ServerException, ConnectionException {
        queryServer("db", QueryMethod.DELETE, null, null);
    }

    /**
     * Connect to the server for gameplay
     *
     * @param gameID Game id to connect to
     * @return GameConnection allowing user to send UserCommands and register handlers for Server Messages
     * @throws ConnectionException Couldn't connect
     */
    public GameConnection connect(int gameID) throws ConnectionException {
        return new GameConnection(Main.SERVER_WS_ADDRESS, token, gameID);
    }

    /**
     * Big boy function that communicates with the server based on the following parameters
     *
     * @param path          The relative path from the given url
     * @param method        Get, Post, etc.
     * @param request       Object to be gson serialized. Null if there is no body
     * @param responseClass The Object to which GSON deserializes. Null if no response is expected
     * @param <T>           Response Class
     * @return Deserialized Class
     * @throws ConnectionException Couldn't connect
     * @throws ServerException     non 200 response (Contains error message)
     */
    private <T> T queryServer(String path, QueryMethod method, Object request, Class<T> responseClass) throws ConnectionException, ServerException {
        try {
            URI fullPath = url.resolve(path);
            HttpURLConnection http = (HttpURLConnection) fullPath.toURL().openConnection();
            http.setRequestMethod(method.toString());
            if (token != null) http.addRequestProperty("authorization", token);
            if (request != null) {
                http.setDoOutput(true);
                http.addRequestProperty("Content-Type", "application/json");
                try (var outputStream = http.getOutputStream()) {
                    String jsonBody = new Gson().toJson(request);
                    outputStream.write(jsonBody.getBytes());
                }
            }
            http.connect();

            if (http.getResponseCode() != Status.SUCCESS) {
                throw new ServerException(http);
            }

            if (responseClass == null) return null;

            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                return new Gson().fromJson(inputStreamReader, responseClass);
            }

        } catch (IOException e) {
            throw new ConnectionException(e.getMessage());
        }
    }
}
