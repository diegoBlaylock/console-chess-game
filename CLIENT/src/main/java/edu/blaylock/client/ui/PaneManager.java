package edu.blaylock.client.ui;

import edu.blaylock.client.ui.components.base.Component;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.WindowBufferSizeListener;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;
import java.util.function.IntConsumer;

/**
 * Controls rendering to the screen. Allows multiple components to stack on top of each other, but only the top one
 * will be in focus and render. Allows for pushing and popping components. Because it controls rendering, it also will
 * set the cursor location if it is visible.
 */
public class PaneManager {
    private final static Stack<Component> componentStack = new Stack<>();
    public static final String COMPONENT_LOCK = "COMPONENT_LOCK";
    private static int curX, curY;

    static {
        Terminal.dispatcher.addListener(Record.WINDOW_BUFFER_SIZE_EVENT,
                (WindowBufferSizeListener) e -> {
                    synchronized (COMPONENT_LOCK) {
                        if (!componentStack.empty())
                            componentStack.peek().invalidate();
                    }
                });
    }

    public static void render() {
        synchronized (COMPONENT_LOCK) {
            if (componentStack.empty()) return;
            Component current = componentStack.peek();
            Graphics g = new Graphics();
            g.setPosition(0, 0);
            synchronized (Component.PAINT_LOCK) {
                if (current.isDirty()) {
                    resetRender(g);
                } else {
                    g.pushRect(current.getRect());
                    current.paint(g);
                    g.popRect();
                    g.reset();
                }
            }
            g.setPosition(curY, curX);
        }
    }

    public static void pushComponent(Component component) {
        synchronized (COMPONENT_LOCK) {
            if (!componentStack.empty()) componentStack.peek().setFocus(false);
            componentStack.push(component);
            component.setFocus(true);
            component.invalidate();
        }
    }

    public static void popComponent() {
        synchronized (COMPONENT_LOCK) {
            if (!componentStack.empty()) componentStack.pop().close();
            if (!componentStack.empty()) componentStack.peek().setFocus(true);
            componentStack.peek().invalidate();
        }
    }

    public static void setCursorPos(int row, int col) {
        curX = col;
        curY = row;
    }

    public static Collection<Component> getComponents() {
        return Collections.unmodifiableCollection(componentStack);
    }

    public static void swapAllComponent(Component component) {
        componentStack.peek().setFocus(false);
        synchronized (COMPONENT_LOCK) {
            componentStack.forEach(Component::close);
            componentStack.clear();
        }
        pushComponent(component);
        component.invalidate();
    }

    public static void swapLastComponent(Component component) {

        synchronized (COMPONENT_LOCK) {
            if (!componentStack.empty())
                componentStack.pop().close();
            componentStack.push(component);
            component.setFocus(true);
            component.invalidate();
        }
    }

    public static void prompt(IntConsumer callback, String prompt, String[] options) {
        pushComponent(new Prompt(callback, prompt, options));
    }

    private static void resetRender(Graphics graphics) {
        Terminal.getInstance().out.print_flush("\u001b[2J");
        Component component = componentStack.peek();
        graphics.pushRect(component.getRect());
        component.paint(graphics);
        graphics.popRect();
        graphics.reset();
    }
}
