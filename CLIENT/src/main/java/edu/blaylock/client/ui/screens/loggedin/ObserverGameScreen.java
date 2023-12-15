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
 * Join a game as an observer
 */
public class ObserverGameScreen extends Pane {

    TextField number;

    Label footer = new Footer();

    public ObserverGameScreen() {
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

    ArrowFocusManager focusManager = new ArrowFocusManager(1, 1);

    void addTitle(Pane pane) {
        Label title = getTitle();
        pane.addComponent(title);
    }

    private static Label getTitle() {
        Label title = new Label("Join Game as Observer");
        title.shrink();
        title.offset(-1, 4);
        return title;
    }

    void addSelections(Container container) {
        number = UIUtils.getTextField("Game ID:", 6, false, container);

        focusManager.addComponent(0, 0, number);
    }

    void addSubmitButton(Container container) {
        Button button = new Button("Observe", this::submit, UIUtils.Justify.CENTER);
        button.offset(-1, 10);
        button.size(42, 3);
        container.addComponent(button);
        focusManager.addComponent(1, 0, button);
    }

    void submit() {
        footer.setText("Joining Game...");
        try {
            int gameID = Integer.parseInt(number.getText());
            Main.SERVER.joinGame("", gameID);
            PaneManager.swapLastComponent(new EscapeWrapper(new GameScreen("OBSERVER", gameID), "EXIT"));

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
