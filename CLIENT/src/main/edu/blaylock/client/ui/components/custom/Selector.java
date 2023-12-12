package edu.blaylock.client.ui.components.custom;

import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.GraphicsOptions;
import edu.blaylock.client.ui.Rect;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Component;
import edu.blaylock.client.ui.components.base.Label;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.KeyEvent;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

import java.util.ArrayList;
import java.util.List;

import static edu.blaylock.terminal.events.KeyCode.*;

/**
 * Allows for a vertical list of options that can be selected with return
 */
public class Selector extends Component {


    private final List<Selection> selectionList = new ArrayList<>();
    private final KeyListener eventListener = this::handleEvent;
    private int selection = 0;
    private final UIUtils.Justify justify;

    private volatile boolean locked = false;


    public Selector(UIUtils.Justify justify) {
        super();
        Terminal.dispatcher.addListener(Record.KEY_EVENT, eventListener);

        this.justify = justify;
        setFocus(true);
    }

    public void addSelection(String string, Runnable runnable) {

        Label label = new Label(string, justify);
        label.shrink();
        Component comp = new BorderWrapper(label, 0, 0);
        comp.size(getRect().width(), comp.getRect().height());
        selectionList.add(new Selection(comp, runnable));
        invalidate();
    }

    public void handleEvent(KeyEvent keyEvent) {
        if (!getFocus() || locked) return;
        if ((keyEvent.controlKeyState & CTRL) != 0 || keyEvent.keyDown) return;

        switch (keyEvent.virtualKeyCode) {
            case VK_UP:
                synchronized (this) {
                    selectionList.get(selection).component.invalidate();
                    selection -= (selection == 0) ? 0 : 1;
                    selectionList.get(selection).component.invalidate();
                    break;
                }
            case VK_DOWN:
                synchronized (this) {
                    selectionList.get(selection).component.invalidate();
                    selection += (selection == selectionList.size() - 1) ? 0 : 1;
                    selectionList.get(selection).component.invalidate();
                    break;
                }
            case VK_RETURN:
                keyEvent.consume();
                selectionList.get(selection).runnable.run();
                break;
        }
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        synchronized (this) {
            int height = 0;
            for (int i = 0; i < selectionList.size(); i++) {
                graphics.pushRect(new Rect(0, height, -1, -1));
                if (i == selection) graphics.setAttributes(GraphicsOptions.REVERSE);
                Selection comp = selectionList.get(i);
                height += comp.component.getRect().height();
                graphics.pushRect(comp.component().getRect());
                if (comp.component.isDirty()) comp.component.paint(graphics);
                graphics.popRect();
                if (i == selection) graphics.setAttributes(GraphicsOptions.UN_REVERSE);
                graphics.popRect();
            }
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
    }

    @Override
    public void invalidate() {
        super.invalidate();
        selectionList.forEach((c) -> c.component.invalidate());
    }

    public void setLocked(boolean lock) {
        this.locked = lock;
    }

    record Selection(
            Component component,
            Runnable runnable
    ) {
    }
}
