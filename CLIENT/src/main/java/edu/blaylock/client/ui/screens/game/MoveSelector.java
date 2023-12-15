package edu.blaylock.client.ui.screens.game;

import chess.ChessMove;
import chess.ChessPosition;
import edu.blaylock.chess.impl.ChessMoveImpl;
import edu.blaylock.chess.impl.ChessPositionImpl;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.KeyCode;
import edu.blaylock.terminal.events.KeyEvent;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

import java.util.function.Consumer;

public class MoveSelector {
    private final Object mutex = new Object();

    private boolean enabled = false;
    private ChessPosition startPosition;
    private ChessPosition endPosition = null;

    private boolean flipped;

    private final Consumer<ChessMove> moveCallback;
    private final Consumer<ChessMove> submitCallback;


    private final KeyListener keyListener = this::consumeKeyEvent;

    public MoveSelector(Consumer<ChessMove> moveCallback, Consumer<ChessMove> submitCallback, boolean flipped) {
        this(1, 1, moveCallback, submitCallback, flipped);
    }

    public MoveSelector(int row, int col, Consumer<ChessMove> moveCallback, Consumer<ChessMove> submitCallback, boolean flipped) {
        this.startPosition = new ChessPositionImpl(row, col);
        this.moveCallback = moveCallback;
        this.submitCallback = submitCallback;
        this.flipped = flipped;
        Terminal.dispatcher.addListener(Record.KEY_EVENT, keyListener);
    }

    public void enable() {
        this.enabled = true;
        sendMoveEvent();
    }

    public void disable() {
        this.enabled = false;
    }

    public void reset() {
        synchronized (mutex) {
            endPosition = null;
            startPosition = new ChessPositionImpl(1, 1);
            if (enabled) sendMoveEvent();
        }
    }

    private void consumeKeyEvent(KeyEvent event) {
        if (!enabled) return;
        synchronized (mutex) {
            if (endPosition == null) {
                ChessPosition newPosition;
                if ((newPosition = processArrowKeys(event, startPosition)) != null) {
                    startPosition = newPosition;
                    sendMoveEvent();
                    return;
                }

                if (!event.keyDown && event.virtualKeyCode == KeyCode.VK_RETURN) {
                    endPosition = startPosition.offset(0, 0);
                    sendMoveEvent();
                }
            } else {
                ChessPosition newPosition;
                if ((newPosition = processArrowKeys(event, endPosition)) != null) {
                    endPosition = newPosition;
                    sendMoveEvent();
                    return;
                }

                if (!event.keyDown && event.virtualKeyCode == KeyCode.VK_RETURN) {
                    event.consume();
                    disable();
                    submitCallback.accept(getMove());
                }
            }

        }
    }

    private ChessPosition getPosition(ChessPosition position) {
        if (position == null) return null;
        return new ChessPositionImpl(
                !flipped ? position.getRow() : (9 - position.getRow()),
                !flipped ? position.getColumn() : (9 - position.getColumn()));
    }

    private ChessMove getMove() {
        return new ChessMoveImpl(getPosition(startPosition), getPosition(endPosition), null);
    }

    private void sendMoveEvent() {
        moveCallback.accept(getMove());
    }

    private void setFlipped(boolean flipped) {
        synchronized (mutex) {
            this.flipped = flipped;
        }
    }

    private ChessPosition processArrowKeys(KeyEvent event, ChessPosition position) {
        if (!event.keyDown) return null;
        return switch (event.virtualKeyCode) {
            case KeyCode.VK_UP -> (position.getRow() < 8) ? position.offset(1, 0) : null;
            case KeyCode.VK_DOWN -> (position.getRow() > 1) ? position.offset(-1, 0) : null;
            case KeyCode.VK_LEFT -> (position.getColumn() > 1) ? position.offset(0, -1) : null;
            case KeyCode.VK_RIGHT -> (position.getColumn() < 8) ? position.offset(0, 1) : null;
            default -> null;
        };
    }

    public void flip() {
        setFlipped(!flipped);
    }
}
