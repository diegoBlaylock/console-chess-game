package edu.blaylock.client.ui.components.base;

import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.components.focusmanagers.FocusManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class able to hold more than one component and manage their repainting. Focus of the child components is managed
 * by a Focus Manager. A null focus manager (none set) will give focus to all components when the container comes into
 * focus.
 */
public abstract class Container extends Component {
    Collection<Component> components = new ArrayList<>();
    FocusManager focusManager = null;

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        components.forEach(c -> {
            if (c.isVisible()) {
                graphics.setAttributes(graphicsOptions);
                graphics.pushRect(c.rect);
                c.paint(graphics);
                graphics.popRect();
            }
        });
        if (focusManager != null) focusManager.invalidate();
    }

    public void addComponent(Component component) {
        components.add(component);
        component.setFocus(getFocus());
        component.parent = this;
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        components.forEach(Component::invalidate);
        if (focusManager != null) focusManager.invalidate();
    }

    public void setFocusManager(FocusManager manager) {
        focusManager = manager;
    }

    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
        if (focusManager != null) {
            if (focus) focusManager.receiveFocus();
            else focusManager.loseFocus();
        } else {
            components.forEach(c -> c.setFocus(focus));
        }
    }

    @Override
    public void close() {
        components.forEach(Component::close);
        super.close();
    }
}
