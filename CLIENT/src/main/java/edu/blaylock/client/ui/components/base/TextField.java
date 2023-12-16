package edu.blaylock.client.ui.components.base;

import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.PaneManager;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.KeyEvent;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

import static edu.blaylock.terminal.events.KeyCode.*;

/**
 * Is able to accept input when in focus. It is important that if there are multiple textfield on a single
 * container that a focusmanager only gives focus to one at a time. If the textfield is protected, it will only render
 * asterisk characters on the screen
 */
public class TextField extends Component {
    protected StringBuilder string = new StringBuilder();
    protected int cursor = 0;
    protected int scroll_x = 0;

    private boolean protect = false;

    protected final KeyListener eventListener = this::handleKeyEvent;

    public TextField(boolean protect) {
        this();
        this.protect = protect;
    }

    public TextField() {
        Terminal.dispatcher.addListener(Record.KEY_EVENT, eventListener);
    }

    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
        if (focus) Terminal.getInstance().out.printFlush("\u001b[?25h");
        else Terminal.getInstance().out.printFlush("\u001b[?25l");
    }

    @Override
    public void paintComponent(Graphics graphics) {
        graphics.fillRect(0, 0, graphics.width(), graphics.height());
        if (protect)
            graphics.drawString(0, 0, "*".repeat(string.length() - scroll_x));
        else
            graphics.drawString(0, 0, string.substring(scroll_x));
        PaneManager.setCursorPos(graphics.getRect().y(), graphics.getRect().x() + cursor - scroll_x);
    }

    public String getText() {
        return string.toString();
    }

    private void handleKeyEvent(KeyEvent event) {
        if (!getFocus() || !event.keyDown) return;
        if (event.character >= ' ') {
            string.insert(cursor, event.character);
            cursor++;
            if (cursor - scroll_x >= getRect().width()) scroll_x++;
            invalidate();
        } else if (event.virtualKeyCode == VK_BACK) {
            if (cursor == 0) return;
            cursor--;
            string.deleteCharAt(cursor);
            if (scroll_x > 0) scroll_x--;
            invalidate();
        } else if (event.virtualKeyCode == VK_DELETE) {
            if (cursor == string.length()) return;
            string.deleteCharAt(cursor);
            invalidate();
        } else if (event.virtualKeyCode == VK_LEFT) {
            if (cursor == 0) return;
            cursor--;
            if (cursor < scroll_x) scroll_x--;
            invalidate();
        } else if (event.virtualKeyCode == VK_RIGHT) {
            if (cursor == string.length()) return;
            cursor++;
            if (cursor - scroll_x >= getRect().width()) scroll_x++;
            invalidate();
        }
    }
}
