package edu.blaylock.utils.gson;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.blaylock.utils.gson.adapters.*;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

/**
 * Class containing pre-initialized Gson instances with modifications for this project
 */
public class GsonUtils {
    /**
     * Standard gson, excludes transient, null values, and Transient annotations
     */
    static final Gson STANDARD;

    /**
     * gson that excludes transient and Transient annotations
     */
    static final Gson ALLOW_NULLS;

    static {
        GsonBuilder baseBuilder = new GsonBuilder()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardAdapter())
                .registerTypeAdapter(ChessGame.class, new ChessGameAdapter())
                .registerTypeAdapter(ChessMove.class, new ChessMoveAdapter())
                .registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter())
                .registerTypeAdapter(ServerMessage.class, new ServerMessageAdapter())
                .registerTypeAdapter(UserGameCommand.class, new UserCommandAdapter())
                .addSerializationExclusionStrategy(new TransientAnnotationStrategy());

        STANDARD = baseBuilder.create();
        ALLOW_NULLS = baseBuilder.serializeNulls().create();
    }

    /**
     * Standard gson, excludes transient, null values, and Transient annotations
     *
     * @return gson
     */
    public static Gson standard() {
        return STANDARD;
    }

    /**
     * gson that excludes transient and Transient annotations
     *
     * @return gson
     */
    public static Gson allowNulls() {
        return ALLOW_NULLS;
    }
}
