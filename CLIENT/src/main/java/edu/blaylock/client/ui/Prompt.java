package edu.blaylock.client.ui;

import edu.blaylock.client.ui.components.base.Label;
import edu.blaylock.client.ui.components.base.Pane;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.KeyCode;
import edu.blaylock.terminal.events.KeyEvent;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

import java.util.function.IntConsumer;

/**
 * Basic prompt allowing users to select options with the keyboard and submit them. Automatically pops the prompt from
 * the PaneManager
 */
public class Prompt extends Pane {
    private final Label[] options;
    private final IntConsumer callback;
    private int selected = 0;

    private final KeyListener listener = this::processKeyEvent;

    public Prompt(IntConsumer callback, String prompt, String[] options) {
        this.callback = callback;
        this.options = new Label[options.length];

        Pane optionsPane = new Pane();

        int optionsWidth = 0;
        for (int i = 0; i < options.length; i++) {
            this.options[i] = createOptionLabel(options[i], optionsWidth);
            optionsPane.addComponent(this.options[i]);
            optionsWidth += options[i].length() + 1;
        }

        optionsPane.translate(-1, 3);
        optionsPane.size(optionsWidth, 1);
        addComponent(optionsPane);

        size(Math.max(optionsWidth, prompt.length()), 5);
        translate(-1, -1);

        Label promptLabel = new Label(prompt, UIUtils.Justify.CENTER);
        promptLabel.size(-1, 1);
        promptLabel.translate(0, 1);
        promptLabel.setGraphicsOptions(GraphicsOptions.RESET);
        addComponent(promptLabel);
        Terminal.dispatcher.addListener(Record.KEY_EVENT, listener);
        updateOptions(selected);
    }

    private void processKeyEvent(KeyEvent event) {
        if (!getFocus()) return;

        switch (event.virtualKeyCode) {
            case KeyCode.VK_LEFT -> {
                if (selected > 0 && event.keyDown) updateOptions(selected - 1);
            }
            case KeyCode.VK_RIGHT -> {
                if (selected < options.length - 1 && event.keyDown) updateOptions(selected + 1);
            }
            case KeyCode.VK_RETURN -> {
                if (!event.keyDown) {
                    event.consume();
                    setFocus(false);
                    callback.accept(selected);
                    PaneManager.popComponent();
                }
            }
        }
    }

    private Label createOptionLabel(String string, int location) {
        Label label = new Label(string);
        label.shrink();
        label.translate(location, 0);
        label.setGraphicsOptions(GraphicsOptions.RESET);
        return label;
    }

    private void updateOptions(int newOption) {
        options[selected].setGraphicsOptions(GraphicsOptions.RESET);
        options[newOption].setGraphicsOptions(GraphicsOptions.FG_BLACK, GraphicsOptions.BG_WHITE);
        options[selected].invalidate();
        options[newOption].invalidate();
        selected = newOption;
    }


}
