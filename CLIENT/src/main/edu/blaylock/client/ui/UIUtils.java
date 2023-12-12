package edu.blaylock.client.ui;

import edu.blaylock.client.ui.components.base.Container;
import edu.blaylock.client.ui.components.base.Label;
import edu.blaylock.client.ui.components.base.TextField;

public class UIUtils {
    public static final String DEFAULT_MESSAGE = "Press Ctrl-C to quit, Ctrl-H for help screen...";

    public static final String CONNECTION_ERROR = "❌ Couldn't connect to server...";

    public static String[] getLines(String string) {
        return string.replaceAll("\t", "    ").replaceAll("\r", "").split("\n");
    }

    public static TextField getTextField(String name, int y, boolean protect, Container container) {
        Label label = new Label(name);
        label.shrink();
        TextField field = new TextField(protect);
        field.size(32, 1);
        label.translate(0, y);
        field.translate(label.getRect().width() + 1, y);
        container.addComponent(label);
        container.addComponent(field);

        return field;
    }

    public enum Justify {
        LEFT, CENTER;
    }
}
