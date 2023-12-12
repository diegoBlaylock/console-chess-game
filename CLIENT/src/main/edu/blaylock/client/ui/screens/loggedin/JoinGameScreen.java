package edu.blaylock.client.ui.screens.loggedin;

import edu.blaylock.client.Main;
import edu.blaylock.client.facade.exceptions.ConnectionException;
import edu.blaylock.client.facade.exceptions.ServerException;
import edu.blaylock.client.ui.PaneManager;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.*;
import edu.blaylock.client.ui.components.custom.EscapeWrapper;
import edu.blaylock.client.ui.components.custom.Footer;
import edu.blaylock.client.ui.components.focusmanagers.ArrowFocusManager;
import edu.blaylock.client.ui.screens.game.GameScreen;

/**
 * Text fields for joining a game.
 * TODO: Replace color options with a key selector (Prevent user from typing whatever)
 */
public class JoinGameScreen extends Pane {
    TextField number;
    TextField color;

    Label footer = new Footer();

    public JoinGameScreen() {
        super();
        size(-1, -1);
        translate(0, 0);

        Pane pane = new Pane();
        pane.size(40, 32);
        pane.translate(-1, -1);
        addTitle(pane);
        addSelections(pane);
        addSubmitButton(pane);

        addComponent(footer);

        setFocusManager(focusManager);

        addComponent(pane);
    }

    ArrowFocusManager focusManager = new ArrowFocusManager(1, 1, 1);

    void addTitle(Pane pane) {
        Label title = getTitle();
        pane.addComponent(title);
    }

    private static Label getTitle() {
        Label title = new Label("Join Game");
        title.shrink();
        title.offset(-1, 4);
        return title;
    }

    void addSelections(Container container) {
        number = UIUtils.getTextField("Game ID:", 6, false, container);
        color = UIUtils.getTextField("Color (WHITE/BLACK):", 7, false, container);

        focusManager.addComponent(0, 0, number);
        focusManager.addComponent(1, 0, color);
    }

    void addSubmitButton(Container container) {
        Button button = new Button("Join", this::submit, UIUtils.Justify.CENTER);
        button.offset(-1, 10);
        button.size(42, 3);
        container.addComponent(button);
        focusManager.addComponent(2, 0, button);
    }

    void submit() {
        footer.setText("Joining Game...");
        try {
            String color = this.color.getText();
            int gameID = Integer.parseInt(number.getText());
            if (!color.equals("BLACK") && !color.equals("WHITE")) {
                footer.setText("❌ Couldn't interpret desired color, enter \"BLACK\" or \"WHITE\"...");
            } else {
                Main.SERVER.joinGame(color, gameID);
                PaneManager.swapLastComponent(new EscapeWrapper(new GameScreen(color, gameID), "EXIT"));
            }
        } catch (ServerException e) {
            footer.setText("❌ " + e.message());
        } catch (ConnectionException e) {
            footer.setText(UIUtils.CONNECTION_ERROR);
        } catch (NumberFormatException e) {
            footer.setText("❌ Couldn't read in Game ID as integer...");
        }
        invalidate();
    }

}
