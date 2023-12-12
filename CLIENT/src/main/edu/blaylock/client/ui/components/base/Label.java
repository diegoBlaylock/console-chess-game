package edu.blaylock.client.ui.components.base;

import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.UIUtils;

/**
 * Component that displays text.
 */
public class Label extends Component {

    String[] lines;
    UIUtils.Justify justify;

    public Label(String message, UIUtils.Justify justify) {
        lines = UIUtils.getLines(message);
        this.justify = justify;
    }

    public Label(String message) {
        this(message, UIUtils.Justify.LEFT);
    }

    /**
     * shrink size to text size
     */
    public void shrink() {
        int max_width = 0;
        for (String line : lines) {
            if (line.length() > max_width) {
                max_width = line.length();
            }
        }
        size(max_width, lines.length);
    }

    public int getWidth(String[] string) {
        int ret = 0;
        for (String str : string) {
            ret = Math.max(ret, str.length());
        }

        return ret;

    }

    /**
     * Sets new text and invalidates the components
     *
     * @param string new text
     */
    public void setText(String string) {
        this.lines = UIUtils.getLines(string);
        invalidate();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        graphics.fillRect();
        switch (justify) {
            case LEFT:
                graphics.drawString(0, 0, lines);
                break;
            case CENTER:
                graphics.drawString((graphics.width() - getWidth(lines)) / 2, 0, lines);
                break;
        }
    }
}
