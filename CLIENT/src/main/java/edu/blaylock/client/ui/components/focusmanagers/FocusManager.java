package edu.blaylock.client.ui.components.focusmanagers;

public abstract class FocusManager {
    protected boolean hasFocus = false;

    public void receiveFocus() {
        hasFocus = true;
    }

    public void loseFocus() {
        hasFocus = false;
    }

    public abstract void invalidate();
}
