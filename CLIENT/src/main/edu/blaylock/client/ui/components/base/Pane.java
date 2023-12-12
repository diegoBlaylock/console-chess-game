package edu.blaylock.client.ui.components.base;

import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.Rect;

/**
 * Very Basic implementation of container that simply paints the background before its children.
 */
public class Pane extends Container {
    @Override
    public void paintComponent(Graphics graphics) {
        graphics.fillRect(new Rect(0, 0, -1, -1));
    }
}
