package edu.blaylock.client.ui.components.base;

import edu.blaylock.client.Main;
import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.GraphicsOptions;
import edu.blaylock.client.ui.Rect;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.custom.BorderWrapper;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.KeyCode;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

/**
 * A simplification of a border wrapper around a label, paints with reverse color if this component is in focus
 * and will run the runnable given when enter selected.
 */
public class Button extends Component {
    private final Component text;
    private boolean submitted = false;
    private final KeyListener eventListener;

    /**
     * @param text     Text to display
     * @param runnable Action tied to this button
     * @param justify  How text is displayed
     */
    public Button(String text, Runnable runnable, UIUtils.Justify justify) {
        Label label = new Label(text, justify);
        this.text = new BorderWrapper(label, 0, 0);
        eventListener = event -> {
            if (!getFocus() || event.keyDown) return;
            if (event.virtualKeyCode == KeyCode.VK_RETURN && !submitted) {
                synchronized (this) {
                    submitted = true;
                    try {
                        runnable.run();
                    } catch (Throwable t) {
                        Main.CLIENT.exception(t);
                    }
                    submitted = false;
                }
            }
        };
        Terminal.dispatcher.addListener(Record.KEY_EVENT, eventListener);
    }

    @Override
    public void setRect(Rect rect) {
        super.setRect(rect);
        this.text.setRect(rect);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        if (getFocus()) graphics.setAttributes(GraphicsOptions.REVERSE);
        text.paint(graphics);
        graphics.setAttributes(GraphicsOptions.UN_REVERSE);
    }
}
