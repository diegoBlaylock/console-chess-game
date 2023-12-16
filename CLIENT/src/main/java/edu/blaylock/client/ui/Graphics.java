package edu.blaylock.client.ui;

import edu.blaylock.jni.structs.Coord;
import edu.blaylock.terminal.Terminal;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Provides methods to help draw text and rectangles on screen. Manages borders when drawing using push and pop
 * rectangle to manage the area of drawing.
 */
public class Graphics {

    Stack<Rect> rectTrace = new Stack<>();
    int lastColumn = 0;

    public Graphics() {
        Coord size = Terminal.getInstance().out.getConsoleBufferInfo().dwsize;
        if (size.x < 1 || size.y < 1)
            rectTrace.push(new Rect(0, 0, 100, 100));
        else
            rectTrace.push(new Rect(0, 0, size.x, size.y));
    }

    public Rect getRect() {
        return rectTrace.peek();
    }

    /**
     * Set the cursor to this position relative to the painting area
     *
     * @param row    row
     * @param column column
     */
    public void setPosition(int row, int column) {
        Rect previous = rectTrace.peek();
        setAbsolutePosition(previous.y() + row, previous.x() + column);
    }

    /**
     * Set cursor at column in painting area
     *
     * @param column column
     */
    public void setColumn(int column) {
        setAbsoluteColumn(rectTrace.peek().x() + column);
    }

    /**
     * Set cursor at row in painting area
     *
     * @param row row
     */
    public void setRow(int row) {
        setAbsolutePosition(rectTrace.peek().y() + row, lastColumn);
    }

    /**
     * Set graphics options. Needs to be reset by the component if changed
     *
     * @param attributes attributes
     */
    public void setAttributes(int... attributes) {
        if (attributes == null) return;
        String attributeString = Arrays.stream(attributes).mapToObj(String::valueOf).collect(Collectors.joining(";"));
        Terminal.getInstance().out.printFlush(String.format("\u001b[%sm", attributeString));
    }

    /**
     * Using 256-color set the foreground painting
     *
     * @param color color
     */
    public void setForeground(Color color) {
        setAttributes(38, 2, color.red(), color.green(), color.blue());
    }

    /**
     * Using 256-color set the background painting
     *
     * @param color color
     */
    public void setBackground(Color color) {
        setAttributes(48, 2, color.red(), color.green(), color.blue());
    }

    /**
     * reset painting options
     */
    public void reset() {
        Terminal.getInstance().out.printFlush("\u001b[0m");
    }

    /**
     * Will push the following boundary to the area stack. If x or y coordinates are negative one, the area will be
     * centered. If these are Integer.MIN_VALUE then they will be moved to the rightmost/bottommost values. If the
     * sizes are negative then they will be sized as a ratio of the previous area e.g. -1 = fill whole area, -2 fill
     * half the area
     *
     * @param rect Relative rect
     */
    public void pushRect(Rect rect) {
        rectTrace.push(interpret(rect));
        setPosition(0, 0);
    }

    /**
     * Will push on a rectangle with x and y padding
     *
     * @param x padding x
     * @param y padding y
     */
    public void pushShrinkRect(int x, int y) {
        Rect rect = rectTrace.peek();
        int new_width = Math.max(0, rect.width() - 2 * x);
        int new_height = Math.max(0, rect.height() - 2 * y);
        rectTrace.push(new Rect(rect.x() + x, rect.y() + y, new_width, new_height));
        setPosition(0, 0);
    }

    /**
     * Remove last area added
     */
    public void popRect() {
        if (rectTrace.size() > 1) {
            rectTrace.pop();
        }
        setPosition(0, 0);
    }

    /**
     * Fill in relative rectangle on screen
     *
     * @param rect rect
     */
    public void fillRect(Rect rect) {
        rect = relative(rect);

        setColumn(rect.x());
        for (int row = 0; row < rect.height(); row++) {
            setRow(rect.y() + row);
            Terminal.getInstance().out.printFlush(" ".repeat(rect.width()));
        }
    }

    /**
     * Fill in whole area
     */
    public void fillRect() {
        fillRect(new Rect(0, 0, -1, -1));
    }

    /**
     * Fill in relative rectangle on screen
     */
    public void fillRect(int x, int y, int width, int height) {
        fillRect(new Rect(x, y, width, height));
    }

    /**
     * Draw text on area at that position
     *
     * @param x      col
     * @param y      row
     * @param string text
     */
    public void drawString(int x, int y, String string) {
        String[] lines = string.replaceAll("\t", "    ").split("\n");
        drawString(x, y, lines);
    }

    /**
     * Draw text on area at that position
     *
     * @param x     col
     * @param y     row
     * @param lines rows of text to display
     */
    public void drawString(int x, int y, String[] lines) {
        setColumn(x);
        for (int i = 0; i < lines.length && y + i < height(); i++) {
            String print = lines[i];
            setRow(y + i);
            if (print.length() > width())
                print = print.substring(0, getRect().width());
            Terminal.getInstance().out.printFlush(print);
        }
    }

    public int width() {
        return rectTrace.peek().width();
    }

    public int height() {
        return rectTrace.peek().height();
    }

    private Rect relative(Rect rect) {
        Rect previous = rectTrace.peek();
        return interpret(rect).offset(-previous.x(), -previous.y());
    }

    private void setAbsolutePosition(int row, int column) {
        Terminal.getInstance().out.printFlush(String.format("\u001b[%d;%dH", row + 1, column + 1));
        lastColumn = column;
    }

    private void setAbsoluteColumn(int column) {
        Terminal.getInstance().out.printFlush(String.format("\u001b[%dG", column + 1));
        lastColumn = column;
    }

    private Rect interpret(Rect rect) {
        Rect previous = rectTrace.peek();

        int newWidth = previous.width();
        int newHeight = previous.height();
        int newX = previous.x();
        int newY = previous.y();

        if (rect.width() < 0) {
            newWidth /= -rect.width();
        } else {
            newWidth = rect.width();
        }

        if (rect.height() < 0) {
            newHeight /= -rect.height();
        } else {
            newHeight = rect.height();
        }

        if (rect.x() < 0) {
            if (rect.x() == Integer.MIN_VALUE) {
                newX += previous.width() - newWidth;
            } else {
                newX += (previous.width() - newWidth) / 2;
            }
        } else {
            newX += rect.x();
        }

        if (rect.y() < 0) {
            if (rect.y() == Integer.MIN_VALUE) {
                newY += previous.height() - newHeight;
            } else {
                newY += (previous.height() - newHeight) / 2;
            }
        } else {
            newY += rect.y();
        }
        return new Rect(newX, newY, newWidth, newHeight);
    }

}
