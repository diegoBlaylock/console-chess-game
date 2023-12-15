package edu.blaylock.client.ui.components.custom;

import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Label;

/**
 * A label that will place itself at the bottom of a container.
 */
public class Footer extends Label {
    public Footer(String message) {
        super(message, UIUtils.Justify.LEFT);
        size(-1, 1);
        translate(0, Integer.MIN_VALUE);
    }

    public Footer() {
        this(UIUtils.DEFAULT_MESSAGE);
    }
}
