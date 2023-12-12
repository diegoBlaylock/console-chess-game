package edu.blaylock.client.ui.components.base;

import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.Rect;

import java.util.Objects;

/**
 * Parent class for all objects capable of being displayed by the PaneManager
 */
public abstract class Component implements AutoCloseable {

    /**
     * Allows processes to wait for painting to finish
     */
    public static final Object PAINT_LOCK = new Object();
    Rect rect = new Rect(0, 0, 0, 0);

    /**
     * Keep track of chain of parents
     */
    protected Container parent = null;

    /**
     * Tie together a component and its wrapper
     */
    protected Wrapper wrapper = null;

    /**
     * Should the component be repainted
     */
    private boolean dirty = true;

    /**
     * Should the component be displayed
     */
    private boolean visible = true;

    /**
     * Do events affect this component
     */
    private boolean inFocus = false;

    protected int[] graphicsOptions = null;

    /**
     * Called by containers and the PaneManager
     *
     * @param graphics Graphics object to user
     */
    public void paint(Graphics graphics) {
        if (isDirty()) {
            validate();
            if (graphicsOptions != null) graphics.setAttributes(graphicsOptions);
            this.paintComponent(graphics);
        }
    }

    /**
     * Use this for custom painting of a component
     *
     * @param graphics Graphics object to use
     */
    public abstract void paintComponent(Graphics graphics);

    /**
     * @return Whether component is in focus.
     */
    public boolean getFocus() {
        return inFocus;
    }

    /**
     * Set Whether component is in focus
     *
     * @param focus Focus
     */
    public void setFocus(boolean focus) {
        inFocus = focus;
    }

    /**
     * Needs repainting or not
     *
     * @return dirty flag
     */
    public boolean isDirty() {
        return dirty;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Mark component for repainting
     */
    public void invalidate() {
        this.dirty = true;
        if (wrapper != null) wrapper.invalidate();
    }

    /**
     * Unmark component for repainting
     */
    public void validate() {
        this.dirty = false;
    }

    /**
     * Mark both this component and its parent for repainting.
     */
    public void invalidateParent() {
        if (parent == null)
            this.invalidate();
        else
            parent.invalidate();
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
        invalidateParent();
    }

    public void resize(int width, int height) {
        setRect(rect.resize(width, height));
    }

    public void offset(int x, int y) {
        setRect(rect.offset(x, y));
    }

    public void size(int width, int height) {
        setRect(rect.size(width, height));
    }

    public void translate(int x, int y) {
        setRect(rect.translate(x, y));
    }

    /**
     * Must be called by a Wrapper
     *
     * @param key     Protected Class required by this
     * @param wrapper Wrapper to include
     */
    public void setWrapper(Wrapper.Key key, Wrapper wrapper) {
        Objects.requireNonNull(key);
        this.wrapper = wrapper;
    }

    public void setGraphicsOptions(int... options) {
        this.graphicsOptions = options;
    }

    public void close() {
        setFocus(false);
        setVisible(false);
    }
}
