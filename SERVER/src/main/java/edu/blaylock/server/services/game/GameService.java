package edu.blaylock.server.services.game;

import chess.ChessGame;
import edu.blaylock.chess.GameState;
import edu.blaylock.chess.impl.ChessBoardFactory;
import edu.blaylock.chess.impl.ChessGameImpl;
import edu.blaylock.server.database.dao.AuthTokenDAO;
import edu.blaylock.server.database.dao.GameDAO;
import edu.blaylock.server.database.models.Game;
import edu.blaylock.server.exceptions.AlreadyTakenException;
import edu.blaylock.server.exceptions.BadRequestException;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.server.services.game.requests.CreateGameRequest;
import edu.blaylock.server.services.game.requests.JoinGameRequest;
import edu.blaylock.server.services.game.responses.CreateGameResponse;
import edu.blaylock.server.services.game.responses.ListGamesResponse;

/**
 * Class containing service methods pertaining to Games
 */
public class GameService {

    /**
     * Return all games in database
     *
     * @return ListGameResponse a wrapper class for an array of GameModels
     * @throws DataAccessException Database error
     */
    public static ListGamesResponse getGamesList() throws DataAccessException {
        GameDAO gameDAO = new GameDAO();
        return new ListGamesResponse(gameDAO.findAll());
    }

    /**
     * Creates a new game in database
     *
     * @param request Wrapper for requested gameName
     * @return Wrapper for gameID
     * @throws DataAccessException Database error
     */
    public static CreateGameResponse createGame(CreateGameRequest request) throws DataAccessException {
        GameDAO gameDAO = new GameDAO();
        Game game = new Game(0, null, null, request.gameName(), new ChessGameImpl(), GameState.UNFINISHED);
        game.game().setBoard(ChessBoardFactory.defaultChessBoard().copy());
        int next_id = gameDAO.createGame(game);
        return new CreateGameResponse(next_id);
    }

    /**
     * Join a created Game
     *
     * @param request Wrapper for user and requested color and gameID
     * @throws BadRequestException   Happens if game with gameID doesn't exist or color is emoty
     * @throws AlreadyTakenException happens if requested color and gameID already exists.
     * @throws DataAccessException   Database error
     */
    public static void joinGame(JoinGameRequest request)
            throws BadRequestException, AlreadyTakenException, DataAccessException {
        GameDAO gameDAO = new GameDAO();
        String username = new AuthTokenDAO().getAuthToken(request.authToken()).username();
        if (gameDAO.getGameById(request.gameID()) == null) throw new BadRequestException();

        if (request.playerColor() != null && !request.playerColor().isEmpty()) {
            try {
                ChessGame.TeamColor colorDesired = ChessGame.TeamColor.valueOf(request.playerColor());
                gameDAO.setGamePlayer(request.gameID(), username, colorDesired);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException();
            }
        }
    }
}
