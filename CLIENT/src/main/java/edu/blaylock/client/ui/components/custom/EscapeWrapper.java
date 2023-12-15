package edu.blaylock.client.ui.components.custom;

import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.PaneManager;
import edu.blaylock.client.ui.components.base.Component;
import edu.blaylock.client.ui.components.base.Wrapper;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

import static edu.blaylock.terminal.events.KeyCode.VK_ESCAPE;

/**
 * Puts a back arrow at the top left and a custom message. When in focus, the escape key will pop the last pane in
 * Pane Manager
 */
public class EscapeWrapper extends Wrapper {
    String message;
    final KeyListener eventListener;

    public EscapeWrapper(Component component, String message) {
        super(component);
        this.message = String.format("❮ %s", message);
        eventListener = event -> {
            if (getFocus()) {
                if (event.virtualKeyCode == VK_ESCAPE && !event.keyDown) {
                    setFocus(false);
                    PaneManager.popComponent();
                }
            }
        };
        Terminal.dispatcher.addListener(Record.KEY_EVENT, eventListener);
    }

    public EscapeWrapper(Component component) {
        this(component, "ESC");
    }


    @Override
    public void paintComponent(Graphics graphics) {
        graphics.drawString(1, 1, message);
    }
}
