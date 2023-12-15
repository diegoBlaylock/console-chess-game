package edu.blaylock.client.ui.screens.game;

import chess.ChessPosition;
import edu.blaylock.chess.impl.ChessPositionImpl;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.KeyCode;
import edu.blaylock.terminal.events.KeyEvent;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

import java.util.function.Consumer;

/**
 * Allows user to select a cell on the chess board using arrow keys. Each time inner state is updated, the callback
 * is called with the new ChessPosition.
 */
public class HighlightSelector {
    private final Object mutex = new Object();

    private boolean enabled = false;
    private int row;
    private int col;

    private boolean flipped;

    private final Consumer<ChessPosition> positionCallback;

    private final KeyListener keyListener = this::consumeKeyEvent;

    public HighlightSelector(Consumer<ChessPosition> callback, boolean flipped) {
        this(0, 0, callback, flipped);
    }

    /**
     * Create new highlight selector (disabled at first) with the selected row and col
     *
     * @param row      row
     * @param col      col
     * @param callback Callback that will be called everytime the selection changes
     * @param flipped  Whether to flip the selection (0,0 is A1 or 8H)
     */
    public HighlightSelector(int row, int col, Consumer<ChessPosition> callback, boolean flipped) {
        this.row = row;
        this.col = col;
        this.positionCallback = callback;
        this.flipped = flipped;
        Terminal.dispatcher.addListener(Record.KEY_EVENT, keyListener);
    }

    /**
     * Send position event and listen to key presses
     */
    public void enable() {
        this.enabled = true;
        sendPositionEvent();
    }

    /**
     * Stop updating state
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * reset selection to 0,0
     */
    public void reset() {
        synchronized (mutex) {
            row = 0;
            col = 0;
            if (enabled) sendPositionEvent();
        }
    }

    /**
     * Flip representation
     */
    public void flip() {
        setFlipped(!flipped);
    }

    /**
     * Notify positionCallback of change
     */
    private void sendPositionEvent() {
        positionCallback.accept(new ChessPositionImpl(!flipped ? (row + 1) : (8 - row), !flipped ? (col + 1) : (8 - col)));
    }

    private void setFlipped(boolean flipped) {
        synchronized (mutex) {
            this.flipped = flipped;
        }
    }

    /// Key Listener ///
    private void consumeKeyEvent(KeyEvent event) {
        if (!event.keyDown || !enabled) return;
        synchronized (mutex) {
            switch (event.virtualKeyCode) {
                case KeyCode.VK_UP:
                    if (row < 7) {
                        row++;
                        sendPositionEvent();
                    }
                    break;
                case KeyCode.VK_DOWN:
                    if (row > 0) {
                        row--;
                        sendPositionEvent();
                    }
                    break;
                case KeyCode.VK_LEFT:
                    if (col > 0) {
                        col--;
                        sendPositionEvent();
                    }
                    break;
                case KeyCode.VK_RIGHT:
                    if (col < 7) {
                        col++;
                        sendPositionEvent();
                    }
                    break;
            }
        }
    }

}
