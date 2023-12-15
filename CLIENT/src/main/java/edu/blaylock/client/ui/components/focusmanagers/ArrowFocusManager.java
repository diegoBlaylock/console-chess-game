package edu.blaylock.client.ui.components.focusmanagers;

import edu.blaylock.client.ui.components.base.Component;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

import static edu.blaylock.terminal.events.KeyCode.*;

/**
 * Allows you to define a grid of components that can be selected with return
 */
public class ArrowFocusManager extends FocusManager {
    private int row = 0;
    private int col = 0;

    private final KeyListener eventListener;

    private final Component[][] layout;

    public ArrowFocusManager(int... layout) {
        this.layout = new Component[layout.length][];
        for (int i = 0; i < layout.length; i++) {
            this.layout[i] = new Component[layout[i]];
        }

        eventListener = event -> {
            if (!hasFocus) return;
            if ((event.controlKeyState & CTRL) != 0 || !event.keyDown) return;

            int dRow = 0, dCol;

            if (event.virtualKeyCode == VK_TAB) {
                if (col == this.layout[row].length - 1) {
                    dCol = -col;
                    if (row == this.layout.length - 1) dRow = -row;
                    else dRow = 1;
                } else {
                    dCol = 1;
                }
            } else {
                dRow = switch (event.virtualKeyCode) {
                    case VK_UP -> (row == 0) ? 0 : -1;
                    case VK_DOWN -> (row == this.layout.length - 1) ? 0 : 1;
                    default -> 0;
                };

                dCol = switch (event.virtualKeyCode) {
                    case VK_LEFT -> (col == 0) ? 0 : -1;
                    case VK_RIGHT -> (col == this.layout[row].length - 1) ? 0 : 1;
                    default -> 0;
                };
            }

            synchronized (this) {
                giveFocus(row + dRow, col + dCol);
            }


        };
        Terminal.dispatcher.addListener(Record.KEY_EVENT, eventListener);
    }

    public void addComponent(int row, int col, Component component) {
        layout[row][col] = component;
    }

    public void giveFocus(int row, int col) {
        if (this.row == row && this.col == col && component(row, col).getFocus()) return;

        setFocus(this.row, this.col, false);
        this.row = row;
        this.col = col;
        setFocus(row, col, true);
        invalidate();

    }

    @Override
    public void receiveFocus() {
        super.receiveFocus();
        setFocus(row, col, true);
        invalidate();
    }

    @Override
    public void loseFocus() {
        super.loseFocus();
        setFocus(row, col, false);
    }

    @Override
    public void invalidate() {
        component(row, col).invalidate();
    }

    private Component component(int row, int col) {
        return layout[row][col];
    }

    private void setFocus(int row, int col, boolean focus) {
        component(row, col).setFocus(focus);
    }
}
