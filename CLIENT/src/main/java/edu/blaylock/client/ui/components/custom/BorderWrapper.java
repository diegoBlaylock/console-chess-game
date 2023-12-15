package edu.blaylock.client.ui.components.custom;

import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.Rect;
import edu.blaylock.client.ui.components.base.Component;
import edu.blaylock.client.ui.components.base.Wrapper;

/**
 * Draws a line border around a component
 */
public class BorderWrapper extends Wrapper {
    int paddingX;
    int paddingY;

    public BorderWrapper(Component comp, int paddingX, int paddingY) {
        super(comp);
        this.paddingX = paddingX;
        this.paddingY = paddingY;
        this.shrink();
    }

    public void shrink() {
        int width = (component.getRect().width() >= 0) ? paddingX + 1 : 0;
        int height = (component.getRect().height() >= 0) ? paddingY + 1 : 0;
        size(component.getRect().width() + 2 * width, component.getRect().height() + 2 * height);
        translate(component.getRect().x(), component.getRect().y());
    }

    @Override
    public void setRect(Rect rect) {
        super.setRect(rect, false);
        component.setRect(getShrunkRect());
    }


    @Override
    public void paint(Graphics graphics) {
        graphics.pushShrinkRect(1 + paddingX, 1 + paddingY);
        Rect saved = component.getRect();
        component.offset(0, 0);
        component.paintComponent(graphics);
        component.setRect(saved);
        graphics.popRect();
        super.paint(graphics, false);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        String h_line = "─".repeat(graphics.width() - 3);
        String v_line = "│\n".repeat(graphics.height() - 2);
        graphics.drawString(1, 0, h_line);
        graphics.drawString(1, graphics.height() - 1, h_line);
        graphics.drawString(0, 0, String.format("┌\n%s└", v_line));
        graphics.drawString(graphics.width() - 2, 0, String.format("┐\n%s┘", v_line));
    }

    private Rect getShrunkRect() {
        int newX = getRect().x();
        int newY = getRect().y();
        int newWidth = getRect().width();
        int newHeight = getRect().height();

        if (newX >= 0) newX += 1 + paddingX;
        if (newY >= 0) newY += 1 + paddingX;
        if (newWidth >= 0) newWidth = Math.max(0, newWidth - 2 * (1 + paddingX));
        if (newHeight >= 0) newHeight = Math.max(0, newHeight - 2 * (1 + paddingY));
        return new Rect(newX, newY, newWidth, newHeight);
    }
}
