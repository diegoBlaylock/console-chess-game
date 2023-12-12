package edu.blaylock.client.ui.screens.game;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import edu.blaylock.chess.GameState;
import edu.blaylock.chess.impl.ChessBoardFactory;
import edu.blaylock.chess.impl.ChessMoveImpl;
import edu.blaylock.chess.impl.ChessPositionImpl;
import edu.blaylock.client.Main;
import edu.blaylock.client.facade.GameConnection;
import edu.blaylock.client.facade.exceptions.ConnectionException;
import edu.blaylock.client.facade.handlers.ServerMessageHandler;
import edu.blaylock.client.ui.PaneManager;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Pane;
import edu.blaylock.client.ui.components.custom.ErasingLabel;
import edu.blaylock.client.ui.components.custom.EscapeWrapper;
import edu.blaylock.client.ui.components.custom.Footer;
import edu.blaylock.client.ui.screens.HelpScreen;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.KeyEvent;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.Collections;

import static edu.blaylock.terminal.events.KeyCode.*;

/**
 * This class controls the whole gamescreen that is seen on the ui. It holds the game connection and hold the
 * GameBoardPane, InfoPane, and OptionsPane. It defines the action to be taken by the Options Pane and also set
 * selections and highlights on the GameBoardPane dependent on the HighlightSelector and the moveSelector.
 * This class is somewhat an organizer and a lot of methods contained within are simply runnables for the various
 * actions to be done by the option panes, selectors, and prompts. Section are divided by usage being:
 * <ol>
 *  <li>Setup</li>
 *  <li>Utility</li>
 *  <li>KeyEvent Callback</li>
 *  <li>ServerMessage Callbacks</li>
 *  <li>OptionPane Callbacks</li>
 *  <li>HighlightSelector Callbacks</li>
 *  <li>MoveSelector Callbacks</li>
 *  <li>Prompt Callbacks</li>
 * </ol>
 */
public class GameScreen extends Pane {
    /**
     * Normal footer text
     */
    private static final String NORMAL_INFO = "Press Ctrl-R to refresh view...";
    /**
     * Footer text when selecting
     */
    private static final String SELECTION_INFO = "Press Ctrl-D to exit selection mode...";
    /**
     * Color of user, passed by JoinGameScreen
     */
    private final ChessGame.TeamColor color;
    /**
     * Current game instance (updated on loadGame)
     */
    private ChessGame game;
    /**
     * General notification
     */
    private final Footer footer = new Footer(NORMAL_INFO);
    /**
     * Gameplay Notification (temporary notification)
     */
    private final ErasingLabel notifications = new ErasingLabel(40, UIUtils.Justify.CENTER);
    /**
     * KeyEvent listener (Terminal Dispatcher only holds weakreferences to the listeners, so this needs to be held here)
     */
    private final KeyListener listener = this::onKeyEvent;
    private GameConnection connection = null;
    private ChessBoardPane board;
    private InfoPane infoPane;
    private OptionsPane optionsPane;
    private HighlightSelector highlightSelector;
    private MoveSelector moveSelector;


    public GameScreen(String color, int gameID) {
        this.color = (color.equals("OBSERVER")) ? null : ChessGame.TeamColor.valueOf(color);
        setupComponent();
        infoPane.setColor(color);

        try {
            connection = Main.SERVER.connect(gameID);
            registerServerMessageCallbacks();
        } catch (ConnectionException exception) {
            Main.CLIENT.exception(exception);
        }

        try {
            connection.joinGame(this.color);
            connection.verbose();
            notifications.setText("Joined Game!");
        } catch (IOException exception) {
            onError("Couldn't connect");
        }
    }


    /// SETUP METHODS ///
    private void setupComponent() {
        size(-1, -1);
        board = new ChessBoardPane(ChessBoardFactory.defaultChessBoard(), (color == ChessGame.TeamColor.BLACK));
        board.translate(-1, -1);
        notifications.translate(0, 2);
        notifications.size(-1, 1);

        addComponent(footer);
        addComponent(notifications);
        addComponent(board);

        addComponent(createInfoPane());
        addComponent(createOptionsPane());

        highlightSelector = new HighlightSelector(this::highlightChanged, (color == ChessGame.TeamColor.BLACK));
        moveSelector = new MoveSelector(this::moveChanged, this::moveSubmitted, (color == ChessGame.TeamColor.BLACK));
        Terminal.dispatcher.addListener(Record.KEY_EVENT, listener);

    }

    private InfoPane createInfoPane() {
        InfoPane infoPane = new InfoPane();
        infoPane.translate(Integer.MIN_VALUE, -1);
        infoPane.size(-5, -2);
        this.infoPane = infoPane;
        return infoPane;
    }

    private OptionsPane createOptionsPane() {
        OptionsPane optionsPane = new OptionsPane(this::resign, this::makeMove, this::highlight, this::rotate, this::help, this::leave);
        optionsPane.translate(0, -1);
        optionsPane.resize(-5, -2);
        this.optionsPane = optionsPane;
        return optionsPane;
    }

    private void registerServerMessageCallbacks() {
        connection.register(ServerMessage.ServerMessageType.LOAD_GAME, this::onLoadGame);
        connection.register(ServerMessage.ServerMessageType.NOTIFICATION, this::onNotification);
        connection.register(ServerMessage.ServerMessageType.ERROR, (ServerMessageHandler<ErrorMessage>) this::onError);
    }


    /// UTILITY METHODS ///
    private void updateState(GameUIState state) {
        clearBoard();
        optionsPane.setFocus(true);
        optionsPane.setState(state);
        optionsPane.invalidate();
    }

    private void rotate() {
        board.flip();
        highlightSelector.flip();
        moveSelector.flip();
    }

    private void clearBoard() {
        board.setFocus(false);
        board.highlightMoves(Collections.emptyList());
        board.select(null);
        highlightSelector.disable();
        highlightSelector.reset();
        moveSelector.disable();
        moveSelector.reset();
        infoPane.setSelection(null);
        optionsPane.unlock();
        board.invalidate();
        footer.setText(NORMAL_INFO);
    }

    @Override
    public void close() {
        super.close();
        try {
            connection.leave();
            connection.close();
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
        if (!focus) optionsPane.lock();
        else optionsPane.unlock();
    }


    /// KEY_EVENT CALLBACK ///
    private void onKeyEvent(KeyEvent keyEvent) {
        if (!getFocus() || keyEvent.keyDown) return;

        if (keyEvent.virtualKeyCode == VK_R && (keyEvent.controlKeyState & CTRL) != 0) {
            board.invalidate();
        } else if (keyEvent.virtualKeyCode == VK_D && (keyEvent.controlKeyState & CTRL) != 0) {
            clearBoard();
        }
    }


    /// SERVER MESSAGE CALLBACKS ///
    private void onNotification(NotificationMessage message) {
        notifications.setText(message.message());
    }

    private void onError(ErrorMessage message) {
        onError(message.errorMessage());
    }

    private void onError(String error) {
        notifications.setText("❌ " + error);
    }

    private void onLoadGame(LoadGameMessage message) {
        this.game = message.game();
        board.setBoard(message.game().getBoard());
        infoPane.setName(message.name());
        infoPane.setState(message.state());
        infoPane.setTurn(message.game().getTeamTurn());
        infoPane.setRound(message.game().getBoard().getRound());
        board.highlightMoves(message.game().validMoves(new ChessPositionImpl(1, 4)));

        GameUIState state;
        if (message.state() == GameState.UNFINISHED) {
            if (color == null) state = GameUIState.OBSERVE;
            else state = (color == message.game().getTeamTurn()) ? GameUIState.TURN : GameUIState.NOT_TURN;
        } else state = GameUIState.ENDED;

        updateState(state);
    }


    /// OPTION PANE CALLBACKS ///
    private void makeMove() {
        clearBoard();
        optionsPane.lock();
        moveSelector.enable();
        footer.setText(SELECTION_INFO);
    }

    private void highlight() {
        clearBoard();
        optionsPane.lock();
        highlightSelector.enable();
        footer.setText(SELECTION_INFO);
    }

    private void resign() {
        clearBoard();

        PaneManager.prompt(this::confirmResign, "Do you want to Resign?", new String[]{"No", "Yes"});
    }

    private void help() {
        PaneManager.pushComponent(new EscapeWrapper(new HelpScreen()));
    }

    private void leave() {
        PaneManager.popComponent();
    }


    /// HIGHLIGHT SELECTOR CALLBACK ///
    private void highlightChanged(ChessPosition position) {
        board.select(position);
        board.highlightMoves(game.validMoves(position));
        infoPane.setSelection(position.toString());
        board.invalidate();
    }


    /// MOVE SELECTOR CALLBACK ///
    private void moveChanged(ChessMove move) {
        board.select(move.getStartPosition());
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
        if (piece != null && piece.getTeamColor() == color)
            board.highlightMoves(game.validMoves(move.getStartPosition()));
        else
            board.highlightMoves(Collections.emptyList());
        if (move.getEndPosition() == null) {
            infoPane.setSelection(move.getStartPosition().toString());
        } else {
            infoPane.setSelection(String.format("%s -> %s", move.getStartPosition(), move.getEndPosition()));
            board.addSelection(move.getEndPosition());
        }
        board.invalidate();
    }

    private void moveSubmitted(ChessMove move) {
        try {
            if (game.validMoves(move.getStartPosition()).contains(move)) {
                if (game.shouldPromotionOccur(move))
                    PaneManager.prompt(i -> selectPromotionPiece(i, move), "Pawn promotion choice:", new String[]{"Q", "R", "B", "N"});
                else connection.makeMove(move);
                clearBoard();
            } else {
                onError("Move chosen not valid!");
                moveSelector.enable();
            }
        } catch (IOException e) {
            onError("ERROR COMMUNICATING WITH SERVER.");
        }
    }


    /// PROMPT CALLBACKS ///
    public void confirmResign(int i) {
        try {
            if (i == 1) connection.resign();
        } catch (IOException e) {
            onError("Couldn't communicate with Server");
        }
    }

    public void selectPromotionPiece(int i, ChessMove move) {
        ChessPiece.PieceType type =
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT}[i];
        ChessMove newMove;
        newMove = new ChessMoveImpl(move.getStartPosition(), move.getEndPosition(), type);
        try {
            connection.makeMove(newMove);
        } catch (IOException e) {
            onError("ERROR COMMUNICATING WITH SERVER.");
        }
    }
}
