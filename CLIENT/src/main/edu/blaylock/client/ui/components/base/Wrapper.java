package edu.blaylock.client.ui.components.base;

import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.Rect;

/**
 * Allows for component wrappers that will automatically update its component state with the wrapper.
 */
public abstract class Wrapper extends Component {
    protected static final Key KEY = new Key();
    protected Component component;

    public Wrapper(Component component) {
        this.component = component;
        component.setWrapper(KEY, this);
        setRect(component.getRect(), false);
    }

    @Override
    public void paint(Graphics graphics) {
        paint(graphics, true);
    }

    protected void paint(Graphics graphics, boolean paintComponent) {
        if (paintComponent) component.paint(graphics);
        super.paint(graphics);
    }

    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
        component.setFocus(focus);
    }

    @Override
    public void validate() {
        super.validate();
        if (component.isDirty()) component.validate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!component.isDirty()) component.invalidate();
    }

    @Override
    public void setRect(Rect rect) {
        setRect(rect, true);
    }

    protected void setRect(Rect rect, boolean editComponent) {
        super.setRect(rect);
        if (editComponent) component.setRect(rect);
    }

    @Override
    public void resize(int width, int height) {
        setRect(getRect().resize(width, height));
    }

    @Override
    public void offset(int x, int y) {
        setRect(getRect().offset(x, y));
    }

    @Override
    public void size(int width, int height) {
        setRect(getRect().size(width, height));
    }

    @Override
    public void translate(int x, int y) {
        setRect(getRect().translate(x, y));
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public void close() {
        component.close();
        super.close();
    }

    /**
     * Allows on a wrapper object to set a components wrapper.
     */
    public static class Key {
        private Key() {
        }
    }
}
