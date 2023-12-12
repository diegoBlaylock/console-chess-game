package edu.blaylock.client.ui.components.custom;

import edu.blaylock.client.Main;
import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Label;

/**
 * Will display text for timeToErase ticks before clearing itself.
 */
public class ErasingLabel extends Label {
    private long lastModifiedTick = -1;
    private final long timeToErase;

    public ErasingLabel(long timeToErase, UIUtils.Justify justify) {
        super("", justify);
        this.timeToErase = timeToErase;
    }

    @Override
    public void paint(Graphics graphics) {
        if (lastModifiedTick >= 0 && (Main.CLIENT.tick() - lastModifiedTick) >= timeToErase) {
            lastModifiedTick = -1;
            super.setText("");
        }
        super.paint(graphics);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        lastModifiedTick = Main.CLIENT.tick();
    }
}
